package com.marles.horarioappufps.exception;

public class ScheduleNotFoundException extends CustomEntityNotFoundException{
    public ScheduleNotFoundException(Long id){
        super("Schedule", id);
    }
}
