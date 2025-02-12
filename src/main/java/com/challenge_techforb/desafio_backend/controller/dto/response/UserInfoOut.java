package com.challenge_techforb.desafio_backend.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserInfoOut {
    private Long id;
    private String username;
    private String email;
}
