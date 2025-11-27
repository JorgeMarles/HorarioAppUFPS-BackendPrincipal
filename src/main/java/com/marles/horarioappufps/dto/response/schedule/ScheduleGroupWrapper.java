package com.marles.horarioappufps.dto.response.schedule;

import lombok.Data;

import java.util.LinkedList;

@Data
public class ScheduleGroupWrapper {

    private ScheduleGroupDto scheduleGroup;
    private LinkedList<ScheduleMessage> messages;

    public ScheduleGroupWrapper(ScheduleGroupDto scheduleGroup) {
        this.scheduleGroup = scheduleGroup;
        messages = new LinkedList<>();
    }

    public ScheduleGroupWrapper(String code){
        messages = new LinkedList<>();
        this.scheduleGroup = new ScheduleGroupDto();
        this.scheduleGroup.setId(0L);
        this.scheduleGroup.setCode(code);
        this.scheduleGroup.setName("Grupo Inexistente");
    }

    public void add(String message, MessageType messageType) {
        messages.add(new ScheduleMessage(messageType, message));
    }
}
