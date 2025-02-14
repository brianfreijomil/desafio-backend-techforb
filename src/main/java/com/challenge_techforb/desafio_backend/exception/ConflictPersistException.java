package com.challenge_techforb.desafio_backend.exception;

import lombok.Getter;

@Getter
public class ConflictPersistException extends RuntimeException{

    private String message;

    public ConflictPersistException(String errorMSG) {
        this.message = errorMSG;
    }
}
