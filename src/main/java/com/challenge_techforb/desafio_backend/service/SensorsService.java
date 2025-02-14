package com.challenge_techforb.desafio_backend.service;

import com.challenge_techforb.desafio_backend.controller.dto.request.SensorIn;
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

    public List<SensorOut> getAllSensorsByPlant(Long plantId) {
        //plant
        PlantEntity plant = this.plantRepository.findById(plantId)
                .orElseThrow(()-> new EntityNotFoundException("Planta no encontrada"));
        //sensors
        return this.sensorRepository.findAllByPlant(plant).stream().map(SensorOut::new).collect(Collectors.toList());
    }

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
            return new SensorOut(sensorSaved);

        } catch (Exception err) {
            throw new ConflictPersistException(err.getMessage());
        }
    }

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

    public void disableEnableSensor(Long sensorId) {
        //check sensor
        SensorEntity sensorSaved = this.sensorRepository.findById(sensorId)
                .orElseThrow(()-> new EntityNotFoundException("Sensor no encontrado"));
        //disable/enable sensor
        try {
            sensorSaved.setIsEnabled(!sensorSaved.getIsEnabled());
            this.sensorRepository.save(sensorSaved);
        } catch (Exception err) {
            throw new ConflictPersistException(err.getMessage());
        }
    }
}
