package com.challenge_techforb.desafio_backend.service;

import com.challenge_techforb.desafio_backend.controller.dto.request.SensorIn;
import com.challenge_techforb.desafio_backend.controller.dto.response.ReadingOut;
import com.challenge_techforb.desafio_backend.controller.dto.response.SensorOut;
import com.challenge_techforb.desafio_backend.exception.ConflictPersistException;
import com.challenge_techforb.desafio_backend.persistence.entity.*;
import com.challenge_techforb.desafio_backend.persistence.repository.PlantRepository;
import com.challenge_techforb.desafio_backend.persistence.repository.SensorRepository;
import com.challenge_techforb.desafio_backend.persistence.repository.SummaryReadingsRepository;
import com.challenge_techforb.desafio_backend.persistence.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SensorsService {

    private static final Logger log = LoggerFactory.getLogger(SensorsService.class);
    private final SensorRepository sensorRepository;
    private final PlantRepository plantRepository;
    private final SummaryReadingsRepository summaryReadingsRepository;
    private final UserRepository userRepository;

    /**
     * create initial 8 sensors for a new plant
     *
     * @param plant
     * @return list of sensors
     */
    public List<SensorEntity> createInitialSensors(PlantEntity plant) {
        if (plant != null) {

            List<SensorEntity> sensors = new ArrayList<>();
            //initial sensors
            sensors.add(new SensorEntity(SensorEnum.TEMPERATURE, plant));
            sensors.add(new SensorEntity(SensorEnum.PRESSURE, plant));
            sensors.add(new SensorEntity(SensorEnum.WIND, plant));
            sensors.add(new SensorEntity(SensorEnum.LEVELS, plant));
            sensors.add(new SensorEntity(SensorEnum.ENERGY, plant));
            sensors.add(new SensorEntity(SensorEnum.TENSION, plant));
            sensors.add(new SensorEntity(SensorEnum.CO2, plant));
            sensors.add(new SensorEntity(SensorEnum.OTHER_GASES, plant));

            //initial readings
            sensors.forEach(s -> {
                Set<ReadingEntity> readings = new HashSet<>();
                readings.add(new ReadingEntity(0, AlertTypeEnum.OK));
                readings.add(new ReadingEntity(0, AlertTypeEnum.MEDIUM));
                readings.add(new ReadingEntity(0, AlertTypeEnum.RED));
                s.setReadings(readings);
            });

            try {
                return this.sensorRepository.saveAll(sensors);
            } catch (Exception err) {
                throw new ConflictPersistException(err.getMessage());
            }
        } return null;
    }

    /**
     * get all sensors by plant
     *
     * @param plantId
     * @return list of sensors
     */
    public List<SensorOut> getAllSensorsByPlant(Long plantId) {
        //plant
        PlantEntity plant = this.plantRepository.findById(plantId)
                .orElseThrow(()-> new EntityNotFoundException("Planta no encontrada"));
        //sensors
        return this.sensorRepository.findAllByPlant(plant).stream().map( s ->
                SensorOut.builder()
                        .id(s.getId())
                        .type(s.getType().toString())
                        .isEnabled(s.getIsEnabled())
                        .sensorOk(new ReadingOut(s.getReadings()
                                .stream()
                                .filter(r -> r.getAlertType().toString().equalsIgnoreCase("OK"))
                                .findFirst()
                                .get())
                        )
                        .mediumAlert(new ReadingOut(s.getReadings()
                                .stream()
                                .filter(r -> r.getAlertType().toString().equalsIgnoreCase("MEDIUM"))
                                .findFirst()
                                .get())
                        )
                        .redAlert(new ReadingOut(s.getReadings()
                                .stream()
                                .filter(r -> r.getAlertType().toString().equalsIgnoreCase("RED"))
                                .findFirst()
                                .get())
                        )
                        .build()
        ).collect(Collectors.toList());
    }

    /**
     * update readings of specific sensor by id and update summary readings affected
     *
     * @param sensorId
     * @param input
     * @param username
     * @return sensor updated
     */
    public SensorOut updateSensor(Long sensorId, SensorIn input, String username) {
        //check sensor
        SensorEntity sensorSaved = this.sensorRepository.findById(sensorId)
                .orElseThrow(()-> new EntityNotFoundException("Sensor no encontrado"));

        Set<ReadingEntity> oldReadings = new HashSet<>(); //old values copy to summary function

        sensorSaved.getReadings().forEach(reading ->
            oldReadings.add(ReadingEntity.builder()
                    .id(reading.getId())
                    .value(reading.getValue())
                    .alertType(reading.getAlertType())
                    .build())
        );
        //update sensor readings
        try {
            sensorSaved.getReadings().forEach(reading -> {
                Integer newValue = input.getReadings().stream()
                        .filter(r -> Objects.equals(r.getId(), reading.getId()))
                        .findFirst()
                        .orElseThrow(()-> new EntityNotFoundException("Lectura/Alerta no encontrada"))
                        .getValue();
                reading.setValue(newValue);
            });
            sensorSaved = this.sensorRepository.save(sensorSaved);
            this.updateSummaryReadings(input,oldReadings,username);
            return SensorOut.builder()
                    .id(sensorSaved.getId())
                    .type(sensorSaved.getType().toString())
                    .isEnabled(sensorSaved.getIsEnabled())
                    .sensorOk(new ReadingOut(sensorSaved.getReadings()
                            .stream()
                            .filter(r -> r.getAlertType().toString().equalsIgnoreCase("OK"))
                            .findFirst()
                            .get())
                    )
                    .mediumAlert(new ReadingOut(sensorSaved.getReadings()
                            .stream()
                            .filter(r -> r.getAlertType().toString().equalsIgnoreCase("MEDIUM"))
                            .findFirst()
                            .get())
                    )
                    .redAlert(new ReadingOut(sensorSaved.getReadings()
                            .stream()
                            .filter(r -> r.getAlertType().toString().equalsIgnoreCase("RED"))
                            .findFirst()
                            .get())
                    )
                    .build();

        } catch (Exception err) {
            throw new ConflictPersistException(err.getMessage());
        }
    }

    /**
     * update a summary readings on sensor readings updating
     *
     * @param input
     * @param oldReadings
     * @param username
     */
    private void updateSummaryReadings(SensorIn input, Set<ReadingEntity> oldReadings, String username) {

        //Check user
        UserEntity user = this.userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()-> new EntityNotFoundException("Usuario no encontrado"));
        //summary update context
        SummaryReadingsEntity summary = this.summaryReadingsRepository.findByUser(user)
                .orElseThrow(()-> new EntityNotFoundException("No se encontro el resumen de lecturas/alertas"));

        oldReadings.forEach(reading -> {
            Integer newValue = input.getReadings().stream()
                    .filter(r -> Objects.equals(r.getId(), reading.getId()))
                    .findFirst()
                    .orElseThrow(()-> new EntityNotFoundException("Lectura/alerta no encontrada"))
                    .getValue();
            int difference = (newValue - reading.getValue()); //difference between new and old reading value

            int currentSummaryReadingValue = summary.getReadings().stream()
                    .filter(r -> r.getAlertType().equals(reading.getAlertType()))
                    .findFirst()
                    .orElseThrow(()-> new EntityNotFoundException("Lectura/alerta no encontrada"))
                    .getValue();

            //new summary reading value
            summary.getReadings().stream()
                    .filter(r -> r.getAlertType().equals(reading.getAlertType()))
                    .findFirst()
                    .orElseThrow(()-> new EntityNotFoundException("Lectura/alerta no encontrada"))
                    .setValue(currentSummaryReadingValue + difference);

        });

        this.summaryReadingsRepository.save(summary);
    }

    /**
     * disable / enable sensor logic
     *
     * @param sensorId
     * @param username
     * @return sensor updated
     */
    public SensorOut disableEnableSensor(Long sensorId, String username) {
        //Check user
        UserEntity user = this.userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()-> new EntityNotFoundException("Usuario no encontrado"));
        //check sensor
        SensorEntity sensorSaved = this.sensorRepository.findById(sensorId)
                .orElseThrow(()-> new EntityNotFoundException("Sensor no encontrado"));
        //disable/enable sensor
        try {
            sensorSaved.setIsEnabled(!sensorSaved.getIsEnabled());
            //update summary readings
            this.updateSummaryOnDisableEnableSensor(sensorSaved,user);
            sensorSaved = this.sensorRepository.save(sensorSaved);

            return SensorOut.builder()
                    .id(sensorSaved.getId())
                    .isEnabled(sensorSaved.getIsEnabled())
                    .build();

        } catch (Exception err) {
            throw new ConflictPersistException(err.getMessage());
        }
    }

    /**
     * update a summary readings on change sensor status
     *
     * @param sensorSaved
     * @param user
     */
    private void updateSummaryOnDisableEnableSensor(SensorEntity sensorSaved, UserEntity user) {
        SummaryReadingsEntity summary = this.summaryReadingsRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el resumen de lecturas/alertas"));

        Set<ReadingEntity> readings = summary.getReadings();

        ReadingEntity ok = readings.stream().filter(r -> r.getAlertType().toString().equalsIgnoreCase("OK")).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Reading OK not found"));
        ReadingEntity medium = readings.stream().filter(r -> r.getAlertType().toString().equalsIgnoreCase("MEDIUM")).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Reading MEDIUM not found"));
        ReadingEntity red = readings.stream().filter(r -> r.getAlertType().toString().equalsIgnoreCase("RED")).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Reading RED not found"));

        int okValueChange = sensorSaved.getReadings().stream()
                .filter(r -> r.getAlertType().toString().equalsIgnoreCase("OK"))
                .findFirst()
                .map(ReadingEntity::getValue)
                .orElse(0);

        int mediumValueChange = sensorSaved.getReadings().stream()
                .filter(r -> r.getAlertType().toString().equalsIgnoreCase("MEDIUM"))
                .findFirst()
                .map(ReadingEntity::getValue)
                .orElse(0);

        int redValueChange = sensorSaved.getReadings().stream()
                .filter(r -> r.getAlertType().toString().equalsIgnoreCase("RED"))
                .findFirst()
                .map(ReadingEntity::getValue)
                .orElse(0);

        if (!sensorSaved.getIsEnabled()) {
            ok.setValue(ok.getValue() - okValueChange);
            medium.setValue(medium.getValue() - mediumValueChange);
            red.setValue(red.getValue() - redValueChange);
        } else {
            ok.setValue(ok.getValue() + okValueChange);
            medium.setValue(medium.getValue() + mediumValueChange);
            red.setValue(red.getValue() + redValueChange);
        }

        summary.setReadings(readings);
        this.summaryReadingsRepository.save(summary);
    }

    /**
     * get count of disabled sensors
     * @return count disabled sensors
     */
    public long getCountSensorsDisabled() {
        return this.sensorRepository.countByIsEnabledFalse();
    }
}
