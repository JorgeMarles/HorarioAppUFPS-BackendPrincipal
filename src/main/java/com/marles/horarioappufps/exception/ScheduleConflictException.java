package com.marles.horarioappufps.exception;

import java.util.Map;

public class ScheduleConflictException extends RuntimeException {
    public ScheduleConflictException(String newGroup, String oldGroup) {
        super(String.format("%s tiene conflicto de horario con %s", newGroup, oldGroup));
    }


    public ScheduleConflictException(Map<String, String> conflicts) {
        StringBuilder sb = new StringBuilder();
        //<SubjectGroupTried, SubjectGroupThatOverlapsWith>
        for (Map.Entry<String, String> p : conflicts.entrySet()) {
            sb.append(p.getKey()).append(" tiene conflicto de horario con : ").append(p.getValue()).append("\n");
        }
    }
}
