package com.challenge_techforb.desafio_backend.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthCreateUserIn {

    @NotBlank(message = "Debe ingresar un correo electronico valido")
    @Email(message = "Formato de correo electronico invalido")
    private String email;
    @NotBlank(message = "Debe ingresar un nombre de usuario")
    private String username;
    @NotBlank(message = "Debe ingresar una contraseña valida")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un carácter especial."
    )
    private String password;
//    @Valid
//    private AuthCreateRoleIn roleRequest;
}
