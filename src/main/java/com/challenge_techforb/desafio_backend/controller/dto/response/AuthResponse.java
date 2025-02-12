package com.challenge_techforb.desafio_backend.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthResponse {
    private String username;
    private String message;
    private String jwt;
    private Boolean status;
}
