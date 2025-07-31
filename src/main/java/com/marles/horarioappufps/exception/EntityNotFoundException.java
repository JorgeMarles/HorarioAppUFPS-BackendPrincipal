package com.marles.horarioappufps.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entity, String field, String value) {
        super(String.format("%s entity with %s %s not found", entity, field, value));
    }
    public EntityNotFoundException(String entity, Long id) {
        this(entity, "id", id.toString());
    }
}
