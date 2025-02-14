package com.challenge_techforb.desafio_backend.controller.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SensorIn {
    @NotNull(message = "Las lecturas y alertas son requeridas")
    @NotEmpty(message = "Las lecturas y alertas son requeridas")
    private List<ReadingIn> readings;

}
