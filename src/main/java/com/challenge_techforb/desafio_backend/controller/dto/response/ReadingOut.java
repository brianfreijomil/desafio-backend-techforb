package com.challenge_techforb.desafio_backend.controller.dto.response;

import com.challenge_techforb.desafio_backend.persistence.entity.AlertTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ReadingOut {

    private Long id;
    private Integer value;
    private AlertTypeEnum type;
}
