package com.marles.horarioappufps.exception;

import com.marles.horarioappufps.dto.response.schedule.ScheduleMessage;
import lombok.Getter;
import lombok.experimental.StandardException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class ScheduleConflictException extends RuntimeException {
    private List<String> messages;
    public ScheduleConflictException(String newGroup, String oldGroup) {
        this(String.format("%s tiene conflicto de horario con %s", newGroup, oldGroup));
        this.messages.add(String.format("%s tiene conflicto de horario con %s", newGroup, oldGroup));
    }

    public ScheduleConflictException(String error) {
        super(error);
        this.messages = new LinkedList<>();
    }

    public static String buildErrorMsg(Map<String, String> conflicts) {
        StringBuilder sb = new StringBuilder();
        //<SubjectGroupTried, SubjectGroupThatOverlapsWith>
        for (Map.Entry<String, String> p : conflicts.entrySet()) {
            sb.append(p.getKey()).append(" tiene conflicto de horario con : ").append(p.getValue()).append("\n");
        }
        return sb.toString();
    }

    public ScheduleConflictException(Map<String, String> conflicts) {
        this(buildErrorMsg(conflicts));
        this.messages = conflicts.entrySet().stream().map(e -> String.format("%s tiene conflicto de horario con %s", e.getKey(), e.getValue())).collect(Collectors.toList());
    }

}

