package com.challenge_techforb.desafio_backend.controller.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadingIn {

    @NotNull(message = "El id de la lectura/alerta es requerido")
    private Long id;

    @NotBlank(message = "El tipo de lectura/alerta es requerido")
    private String type;

    @NotNull(message = "El valor de la lectura/alerta es requerido")
    @Min(value = 0, message = "El valor de la lectura/alerta no puede ser negativo")
    private Integer value;

}
