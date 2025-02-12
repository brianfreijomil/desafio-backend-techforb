package com.challenge_techforb.desafio_backend.controller.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthCreateUserIn {

    @NotBlank(message = "Debe ingresar un email")
    private String email;
    @NotBlank(message = "Debe ingresar un nombre de usuario")
    private String username;
    @NotBlank(message = "Debe ingresar una contraseña valida")
    private String password;
    @Valid
    private AuthCreateRoleIn roleRequest;
}
