package com.challenge_techforb.desafio_backend.persistence.repository;

import com.challenge_techforb.desafio_backend.persistence.entity.PlantEntity;
import com.challenge_techforb.desafio_backend.persistence.entity.SensorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorRepository extends JpaRepository<SensorEntity,Long> {

    long countByIsEnabledFalse();

    List<SensorEntity> findAllByPlant(PlantEntity plantEntity);
}
