package com.marles.horarioappufps.exception;

public class GroupNotFoundException extends CustomEntityNotFoundException {
    public GroupNotFoundException(Long id) {
        super("Group", id);
    }

    public GroupNotFoundException(String code) {
        super("Group", "code", code);
    }
}
