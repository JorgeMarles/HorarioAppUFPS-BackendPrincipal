package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.Session;
import com.marles.horarioappufps.model.SubjectGroup;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class SubjectGroupInfoDto {
    private Long id;
    private String code;
    private String teacher;
    private String program;
    private int maxCapacity;
    private int availableCapacity;
    private boolean isCurrentTeacher;
    private List<SessionInfoDto> sessions = new LinkedList<>();

    public SubjectGroupInfoDto(SubjectGroup subjectGroup) {
        this.id = subjectGroup.getId();
        this.code = subjectGroup.getCode();
        this.teacher = subjectGroup.getTeacher();
        this.program = subjectGroup.getProgram();
        this.maxCapacity = subjectGroup.getMaxCapacity();
        this.availableCapacity = subjectGroup.getAvailableCapacity();
        this.isCurrentTeacher = subjectGroup.isCurrentTeacher();
        for (Session session : subjectGroup.getSessions()) {
            sessions.add(new SessionInfoDto(session));
        }
    }
}
