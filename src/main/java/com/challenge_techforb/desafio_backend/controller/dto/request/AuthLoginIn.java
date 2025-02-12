package com.challenge_techforb.desafio_backend.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthLoginIn {

    @NotBlank(message = "Debe ingresar un email")
    private String email; /*por ahora mantengo el login con email basandome en el figma, es confuso el doc de FAQs (dni,username,etc)*/
    @NotBlank(message = "Debe ingresar una contraseña valida")
    private String password;
}
