package com.marles.horarioappufps.exception;

import jakarta.persistence.EntityNotFoundException;

public class CustomEntityNotFoundException extends EntityNotFoundException {
    public CustomEntityNotFoundException(String entity, String field, String value) {
        super(String.format("Entidad %s con %s %s no encontrada", entity, field, value));
    }
    public CustomEntityNotFoundException(String entity, Long id) {
        this(entity, "id", id.toString());
    }
}
