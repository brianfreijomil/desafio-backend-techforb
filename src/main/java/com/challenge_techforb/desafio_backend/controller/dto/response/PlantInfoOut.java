package com.challenge_techforb.desafio_backend.controller.dto.response;

import com.challenge_techforb.desafio_backend.persistence.entity.SensorEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PlantInfoOut {
    private Long id;
    private String name;
    private String country;
    private List<SensorOut> sensors;
    private ReadingOut sensorOk;
    private ReadingOut mediumAlert;
    private ReadingOut redAlert;
}
