package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.Session;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SessionInfoDto {
    private Long id;
    private int day;
    private int beginHour;
    private int endHour;
    private String classroom;

    public SessionInfoDto(Session session) {
        this.id = session.getId();
        this.day = session.getDay();
        this.beginHour = session.getBeginHour();
        this.endHour = session.getEndHour();
        this.classroom = session.getClassroom();
    }
}
