package com.challenge_techforb.desafio_backend.controller.dto.response;

import com.challenge_techforb.desafio_backend.persistence.entity.SensorEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class SensorOut {

    private Long id;
    private String type;
    private Boolean isEnabled;
    private Set<ReadingOut> readings;

    public SensorOut(SensorEntity s) {
        this.id = s.getId();
        this.type = String.valueOf(s.getType());
        this.isEnabled = s.getIsEnabled();
        this.readings = s.getReadings().stream().map(r -> new ReadingOut(r.getId(), r.getValue(), r.getAlertType())).collect(Collectors.toSet());
    }
}
