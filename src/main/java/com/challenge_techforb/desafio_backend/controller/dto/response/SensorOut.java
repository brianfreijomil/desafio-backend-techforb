package com.challenge_techforb.desafio_backend.controller.dto.response;

import lombok.*;


@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class SensorOut {

    private Long id;
    private String type;
    private Boolean isEnabled;
    private ReadingOut sensorOk;
    private ReadingOut mediumAlert;
    private ReadingOut redAlert;

}
