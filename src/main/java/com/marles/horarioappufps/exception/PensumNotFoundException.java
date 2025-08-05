package com.marles.horarioappufps.exception;

public class PensumNotFoundException extends CustomEntityNotFoundException {
    public PensumNotFoundException(Long id) {
        super("Pensum", id);
    }
}
