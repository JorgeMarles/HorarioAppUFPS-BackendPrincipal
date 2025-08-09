package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.Session;
import com.marles.horarioappufps.model.SubjectGroup;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class GroupScheduleDto {
    private Long id;
    private String name;
    private String teacher;
    private String code;
    private String subjectCode;
    private int credits;
    private List<SessionInfoDto> sessions = new LinkedList<>();

    public GroupScheduleDto(SubjectGroup subjectGroup) {
        this.id = subjectGroup.getId();
        this.code = subjectGroup.getCode();
        this.name = subjectGroup.getSubject().getName();
        this.subjectCode = subjectGroup.getSubject().getCode();
        this.credits = subjectGroup.getSubject().getCredits();
        this.teacher = subjectGroup.getTeacher();
        this.sessions = new LinkedList<>();
        for(Session session : subjectGroup.getSessions()) {
            this.sessions.add(new SessionInfoDto(session));
        }
    }
}
