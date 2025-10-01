package com.marles.horarioappufps.exception;

import java.util.UUID;

public class WorkflowNotFoundException extends CustomEntityNotFoundException {
    public WorkflowNotFoundException(UUID uuid) {
        super("Workflow", "uuid", uuid.toString());
    }
}
