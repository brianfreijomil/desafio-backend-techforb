package com.challenge_techforb.desafio_backend.exception;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ConflictExistException extends RuntimeException{

    private String message;
    private String name;

    /**
     * get generic exception - with the entity and name sent
     * @param entity
     * @param name
     */
    public ConflictExistException(String entity, String name) {
        this.message = String.format("Ya existe %s con el nombre %s", entity, name);
    }

    public ConflictExistException(String msg) {
        this.message = msg;
    }

}
