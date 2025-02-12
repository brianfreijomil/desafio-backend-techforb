package com.challenge_techforb.desafio_backend.controller.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthCreateRoleIn {
    @Size(max = 3, message = "El usuario no puede tener mas de 3 roles")
    private List<String> roleListName;
}
