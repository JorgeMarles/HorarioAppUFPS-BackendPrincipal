package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.Subject;
import com.marles.horarioappufps.model.SubjectGroup;
import com.marles.horarioappufps.model.SubjectType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class SubjectInfoDto {
    private Long id;
    private String code;
    private String name;
    private int credits;
    private int hours;
    private int semester;
    private int requiredCredits;
    private SubjectType type;
    private List<SubjectGroupInfoDto> groups = new LinkedList<>();
    private List<SubjectItemDto> requisites = new LinkedList<>();

    public SubjectInfoDto(Subject subject) {
        this.id = subject.getId();
        this.code = subject.getCode();
        this.name = subject.getName();
        this.credits = subject.getCredits();
        this.hours = subject.getHours();
        this.semester = subject.getSemester();
        this.requiredCredits = subject.getRequiredCredits();
        this.type = subject.getType();

        for (SubjectGroup group : subject.getGroups()) {
            this.groups.add(new SubjectGroupInfoDto(group));
        }

        for (Subject requisite : subject.getRequisites()) {
            this.requisites.add(new SubjectItemDto(requisite));
        }
    }
}
