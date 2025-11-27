package com.marles.horarioappufps.dto.response.schedule;

import lombok.Data;

import java.util.LinkedList;

@Data
public class ScheduleGroupWrapper {

    private ScheduleGroupDto group;
    private LinkedList<ScheduleMessage> messages;

    public ScheduleGroupWrapper(ScheduleGroupDto scheduleGroup) {
        this.group = scheduleGroup;
        messages = new LinkedList<>();
    }

    public ScheduleGroupWrapper(String code){
        messages = new LinkedList<>();
        this.group = new ScheduleGroupDto();
        this.group.setId(0L);
        this.group.setCode(code);
        this.group.setName("Grupo Inexistente");
    }

    public void add(String message, MessageType messageType) {
        messages.add(new ScheduleMessage(messageType, message));
    }
}
