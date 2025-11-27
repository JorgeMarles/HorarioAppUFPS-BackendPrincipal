package com.marles.horarioappufps.dto.response.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScheduleMessage {
    private MessageType type;
    private String message;
}

