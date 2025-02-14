package com.challenge_techforb.desafio_backend.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlantIn {
    @NotBlank(message = "Debe ingresar un nombre")
    private String name;
    @NotBlank(message = "Debe ingresar un pais")
    private String country;

}
