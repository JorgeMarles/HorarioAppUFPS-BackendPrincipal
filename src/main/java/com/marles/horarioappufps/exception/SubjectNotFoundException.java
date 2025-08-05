package com.marles.horarioappufps.exception;

public class SubjectNotFoundException extends CustomEntityNotFoundException {
    public SubjectNotFoundException(Long id) {
        super("Subject", id);
    }

    public SubjectNotFoundException(String code) {
        super("Subject", "code", code);
    }
}
