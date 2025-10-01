package com.marles.horarioappufps.exception;

public class JobNotFoundException extends CustomEntityNotFoundException {
    public JobNotFoundException(Long id) {
        super("Job", id);
    }
}
