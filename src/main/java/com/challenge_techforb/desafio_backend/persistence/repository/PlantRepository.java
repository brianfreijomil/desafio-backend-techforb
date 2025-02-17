package com.challenge_techforb.desafio_backend.persistence.repository;

import com.challenge_techforb.desafio_backend.persistence.entity.PlantEntity;
import com.challenge_techforb.desafio_backend.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantRepository extends JpaRepository<PlantEntity,Long> {

    @Query(value = "SELECT \n" +
            "    p.id AS id, " +
            "    r.alert_type AS alertType, " +
            "    COALESCE(SUM(r.value), 0) AS totalValue " +
            "FROM plants p " +
            "LEFT JOIN sensors s ON p.id = s.plant_id " +
            "LEFT JOIN sensors_readings sr ON s.id = sr.sensor_entity_id " +
            "LEFT JOIN readings r ON sr.readings_id = r.id " +
            "WHERE p.user_id =:userId " +
            "AND s.is_enabled = true " +
            "GROUP BY p.id, r.alert_type " +
            "ORDER BY p.id, r.alert_type", nativeQuery = true)
    List<IAllPlantsAndSumReadingsByAlertType> findAllByIdAAndSumReadingAlertType(@Param("userId") Long userId);

    List<PlantEntity> findAllByUser(UserEntity userEntity);

    Boolean existsByNameIgnoreCaseAndCountryIgnoreCase(String name, String country);

}
