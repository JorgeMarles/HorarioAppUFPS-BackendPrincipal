package com.marles.horarioappufps.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marles.horarioappufps.model.Subject;
import com.marles.horarioappufps.model.SubjectGroup;
import com.marles.horarioappufps.model.SubjectType;
import com.marles.horarioappufps.model.User;
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
    @JsonProperty("isCompleted")
    private boolean isCompleted;
    @JsonProperty("canEnroll")
    private boolean canEnroll;
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

    public SubjectInfoDto(Subject subject, User user, int credits){
        this(subject);
        this.isCompleted = user.containsSubject(subject);
        this.canEnroll = user.canEnroll(subject, credits);
    }
}
