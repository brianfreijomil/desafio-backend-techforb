package com.challenge_techforb.desafio_backend.util;

import lombok.Getter;

@Getter
public enum ErrorCatalog {

    ENTITY_NOT_FOUND("ERR_ENTITY_001", "No se ha encontrado "),
    CONFLICT_EXIST("ERR_ENTITY_002", "."),
    INVALID_ENTITY("ERR_ENTITY_003", "Parametros de entidad invalidos."),
    INVALID_TOKEN("ERR_TOKEN_001", " Token invalido: "),
    CONFLICT_ATTRIBUTES("ERR_ATTRIBUTES_001", "."),
    GENERIC_ERROR("ERR_GEN_001", "Un error inesperado a ocurrido, intente de nuevo mas tarde.");

    private final String code;
    private final String message;

    ErrorCatalog(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
