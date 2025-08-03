package com.marles.horarioappufps.exception;

public class UserNotFoundException extends CustomEntityNotFoundException {
    public UserNotFoundException(String field, String value) {
        super("User", field, value);
    }
    public UserNotFoundException(String uid){
        this("uid", uid);
    }
}
