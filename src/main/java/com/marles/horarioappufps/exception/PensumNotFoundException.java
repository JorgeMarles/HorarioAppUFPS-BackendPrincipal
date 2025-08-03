package com.marles.horarioappufps.exception;

import jakarta.persistence.EntityNotFoundException;

public class PensumNotFoundException extends CustomEntityNotFoundException {
    public PensumNotFoundException(Long id) {
        super("Pensum", id);
    }
}
