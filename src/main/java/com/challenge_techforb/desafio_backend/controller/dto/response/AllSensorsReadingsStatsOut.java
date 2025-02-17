package com.challenge_techforb.desafio_backend.controller.dto.response;

import com.challenge_techforb.desafio_backend.persistence.entity.AlertTypeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Builder
@Getter
@Setter
public class AllSensorsReadingsStatsOut {

    private Set<ReadingOut> readings;
    private Integer sensorsDisabled;
}
