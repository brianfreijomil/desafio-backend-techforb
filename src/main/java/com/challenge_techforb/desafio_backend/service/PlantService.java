package com.challenge_techforb.desafio_backend.service;

import com.challenge_techforb.desafio_backend.controller.dto.request.PlantIn;
import com.challenge_techforb.desafio_backend.controller.dto.response.AllSensorsReadingsStatsOut;
import com.challenge_techforb.desafio_backend.controller.dto.response.PlantInfoOut;
import com.challenge_techforb.desafio_backend.controller.dto.response.ReadingOut;
import com.challenge_techforb.desafio_backend.controller.dto.response.SensorOut;
import com.challenge_techforb.desafio_backend.exception.ConflictExistException;
import com.challenge_techforb.desafio_backend.exception.ConflictPersistException;
import com.challenge_techforb.desafio_backend.persistence.entity.*;
import com.challenge_techforb.desafio_backend.persistence.repository.IAllPlantsAndSumReadingsByAlertType;
import com.challenge_techforb.desafio_backend.persistence.repository.PlantRepository;
import com.challenge_techforb.desafio_backend.persistence.repository.SummaryReadingsRepository;
import com.challenge_techforb.desafio_backend.persistence.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PlantService {

    private final SensorsService sensorsService;
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;
    private final SummaryReadingsRepository summaryReadingsRepository;

    /**
     * get plant detail by id
     *
     * @param plantId
     * @return plant detail, sensors associated, readings, etc.
     */
    public PlantInfoOut getPlantDetailById(Long plantId) {
        PlantEntity plant = this.plantRepository.findById(plantId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro la planta"));

        return PlantInfoOut.builder()
                .id(plant.getId())
                .name(plant.getName())
                .country(plant.getCountry())
                .sensors(plant.getSensors().stream().map(s ->
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
                ).collect(Collectors.toList()))
                .build();
    }

    /**
     * get all plants existing
     * @return list of plants
     */
    public List<PlantInfoOut> getAllPlants() {
        return this.plantRepository.findAll().stream().map(plant -> PlantInfoOut.builder()
                .id(plant.getId())
                .name(plant.getName())
                .country(plant.getCountry())
                .build()).collect(Collectors.toList());
    }

    /**
     * get all plants by user
     *
     * @param username
     * @return plants info, with details, sensors, readings
     */
    public List<PlantInfoOut> getAllPlantsByUser(String username) {
        //Check user
        UserEntity user = this.userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()-> new EntityNotFoundException("Usuario no encontrado"));

        List<PlantEntity> plants = this.plantRepository.findAllByUser(user);
        List<IAllPlantsAndSumReadingsByAlertType> plantsStats = this.plantRepository.findAllByIdAAndSumReadingAlertType(user.getId());

        List<PlantInfoOut> list = plants.stream().map(plant -> PlantInfoOut.builder()
        .id(plant.getId())
        .name(plant.getName())
        .country(plant.getCountry())
        .sensors(plant.getSensors().stream().map(s -> SensorOut.builder()
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
                .build()).collect(Collectors.toList())
        )
        .sensorOk(new ReadingOut(plantsStats.stream()
                .filter(p -> p.getId().equals(plant.getId()))
                .filter(r -> r.getAlertType().equalsIgnoreCase("OK"))
                .findFirst().get())
        )
        .mediumAlert(new ReadingOut(plantsStats.stream()
                .filter(p -> p.getId().equals(plant.getId()))
                .filter(r -> r.getAlertType().equalsIgnoreCase("MEDIUM"))
                .findFirst().get())
        )
        .redAlert(new ReadingOut(plantsStats.stream()
                .filter(p -> p.getId().equals(plant.getId()))
                .filter(r -> r.getAlertType().equalsIgnoreCase("RED"))
                .findFirst().get())
        )
        .build()).collect(Collectors.toList());

        return list;
    }

    /**
     * create a new plant, also create 8 sensors for the plant, 3 reading for each sensor, and create a summary readings
     *
     * @param input
     * @param username
     * @return plant created
     */
    @Transactional
    public PlantInfoOut createPlant(PlantIn input, String username) {
        //check unique (plant - country)
        Boolean existByNameAndCountry = this.plantRepository.existsByNameIgnoreCaseAndCountryIgnoreCase(input.getName(), input.getCountry());
        if (existByNameAndCountry) throw new ConflictExistException("Ya existe una planta con el nombre: " + input.getName() + ", para el pais: " + input.getCountry());
        //Check user
        UserEntity user = this.userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()-> new EntityNotFoundException("Usuario no encontrado"));
        //create plant
        try {
            PlantEntity newPlant = new PlantEntity(input.getName(),input.getCountry(),user);
            //create initial sensors
            newPlant.getSensors().addAll(this.sensorsService.createInitialSensors(newPlant));
            newPlant = this.plantRepository.save(newPlant);
            //create summary readings if non-existent
            if (this.summaryReadingsRepository.findByUser(user).isEmpty()) this.createBasicSummary(user);

            //sum readings of sensors of plant
            ReadingOut ok = new ReadingOut(null,0,AlertTypeEnum.OK);
            ReadingOut medium = new ReadingOut(null,0,AlertTypeEnum.MEDIUM);
            ReadingOut red = new ReadingOut(null,0,AlertTypeEnum.RED);
            for(SensorEntity se : newPlant.getSensors()) {
                for(ReadingEntity re : se.getReadings()) {
                    if (re.getAlertType().toString().equalsIgnoreCase("OK")) {
                        ok.setValue(ok.getValue() + re.getValue());
                    }
                    else if (re.getAlertType().toString().equalsIgnoreCase("MEDIUM")) {
                        medium.setValue(medium.getValue() + re.getValue());
                    }
                    else if (re.getAlertType().toString().equalsIgnoreCase("RED")) {
                        red.setValue(red.getValue() + re.getValue());
                    }
                }
            }

            //return plant created with the whole info
            return PlantInfoOut.builder()
                    .id(newPlant.getId())
                    .name(newPlant.getName())
                    .country(newPlant.getCountry())
                    .sensors(newPlant.getSensors().stream().map(s -> SensorOut.builder()
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
                            .build()).collect(Collectors.toList())
                    )
                    .sensorOk(ok)
                    .mediumAlert(medium)
                    .redAlert(red)
                    .build();
        } catch (Exception err) {
            throw new ConflictPersistException(err.getMessage());
        }
    }

    /**
     * update an existing plant by id
     *
     * @param plantId
     * @param input
     * @param username
     * @return plant updated
     */
    public PlantInfoOut updatePlant(Long plantId, PlantIn input, String username) {
        //check plant
        PlantEntity plantSaved = this.plantRepository.findById(plantId)
                .orElseThrow(()-> new EntityNotFoundException("Planta no encontrada"));
        //check unique (plant - country)
        if (!input.getName().equalsIgnoreCase(plantSaved.getName()) || !input.getCountry().equalsIgnoreCase(plantSaved.getCountry())) {
            Boolean existByNameAndCountry = this.plantRepository.existsByNameIgnoreCaseAndCountryIgnoreCase(input.getName(), input.getCountry());
            if (existByNameAndCountry) throw new ConflictExistException("Ya existe una planta con el nombre: " + input.getName() + ", para el pais: " + input.getCountry());
        }
        //edit plant
        try {
            plantSaved.setName(input.getName());
            plantSaved.setCountry(input.getCountry());
            plantSaved = this.plantRepository.save(plantSaved);
            return PlantInfoOut.builder()
                    .id(plantSaved.getId())
                    .name(plantSaved.getName())
                    .country(plantSaved.getCountry())
                    .build();
        } catch (Exception err) {
            throw new ConflictPersistException(err.getMessage());
        }
    }

    /**
     * delete a existing plant by id
     *
     * @param plantId
     * @param username
     */
    public void deletePlant(Long plantId, String username) {
        //Check user
        UserEntity user = this.userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()-> new EntityNotFoundException("Usuario no encontrado"));

        //check plant
        PlantEntity plantSaved = this.plantRepository.findById(plantId)
                .orElseThrow(()-> new EntityNotFoundException("Planta no encontrada"));
        //delete plant
        //Se podria utilizar un soft delete si no se quiere eliminar a planta por completo (por ejemplo deshabilitarla)
        try {
            this.plantRepository.deleteById(plantId);
            //si no tengo plantas asociadas al usuario elimino el summary
            Boolean deleteSummary = this.plantRepository.findAllByUser(user).isEmpty();
            this.updateSummaryOnDeletePlant(user,plantSaved,deleteSummary);
        } catch (Exception err) {
            throw new ConflictPersistException(err.getMessage());
        }
    }

    public AllSensorsReadingsStatsOut getAllSensorsReadingsByUser(String username) {
        //Check user
        UserEntity user = this.userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()-> new EntityNotFoundException("Usuario no encontrado"));
        //todas las lecturas/alertas de cada sensor de cada planta de un usuario
        Optional<SummaryReadingsEntity> summaryOpt = this.summaryReadingsRepository.findByUser(user);
        if (summaryOpt.isEmpty()) {
            return AllSensorsReadingsStatsOut.builder().build();
        }

        Integer sensorsDisabledCount = (int) this.sensorsService.getCountSensorsDisabled();

        try {
            SummaryReadingsEntity summary = summaryOpt.get();
            return AllSensorsReadingsStatsOut.builder()
                    .readings(summary.getReadings().stream().map(r -> new ReadingOut(r.getId(),r.getValue(),r.getAlertType())).collect(Collectors.toSet()))
                    .sensorsDisabled(sensorsDisabledCount)
                    .build();
        } catch (Exception err) {
        throw new ConflictPersistException(err.getMessage());
        }
    }

    /* SUMMARY READINGS CONTEXT */

    /**
     * update a summary on deleted plant
     *
     * @param user
     * @param plant
     * @param deleteSummary
     */
    private void updateSummaryOnDeletePlant(UserEntity user, PlantEntity plant,Boolean deleteSummary) {

        Optional<SummaryReadingsEntity> summary = this.summaryReadingsRepository.findByUser(user);
        if (!summary.isEmpty()) {
            if (!deleteSummary) {
                summary.get().getReadings().forEach(r -> {
                    plant.getSensors().forEach(s -> {
                        Integer plantReadingValue = s.getReadings().stream()
                                .filter(plantReading -> plantReading.getAlertType().equals(r.getAlertType()))
                                .findFirst()
                                .get()
                                .getValue();

                        r.setValue(r.getValue() - plantReadingValue);
                    });
                });
                this.summaryReadingsRepository.save(summary.get());
            } else {
                this.summaryReadingsRepository.deleteById(summary.get().getId());
            }
        }
    }

    /**
     * create a summary readings
     *
     * @param user
     * @return summary created
     */
    private SummaryReadingsEntity createBasicSummary(UserEntity user) {
        //main readings
        Set<ReadingEntity> readings = new HashSet<>();
        readings.add(new ReadingEntity(0, AlertTypeEnum.OK));
        readings.add(new ReadingEntity(0, AlertTypeEnum.MEDIUM));
        readings.add(new ReadingEntity(0, AlertTypeEnum.RED));
        try {
            SummaryReadingsEntity summary = SummaryReadingsEntity.builder()
                    .user(user)
                    .readings(readings)
                    .build();
            return this.summaryReadingsRepository.save(summary);
        } catch (Exception err) {
            throw new ConflictPersistException(err.getMessage());
        }
    }

}
