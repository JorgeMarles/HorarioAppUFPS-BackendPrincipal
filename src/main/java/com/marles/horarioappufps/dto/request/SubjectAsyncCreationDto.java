package com.marles.horarioappufps.dto.request;

import com.marles.horarioappufps.model.SubjectGroup;
import com.marles.horarioappufps.model.SubjectType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class SubjectAsyncCreationDto {
    private String code;
    private String name;
    private int credits;
    private int hours;
    private int semester;
    private int requiredCredits = 0;
    private SubjectType type;
    private Map<String, SubjectGroupCreationDto> groups = new HashMap<>();
    private List<String> requisites;
    private List<String> equivalences;

    public SubjectCreationDto toSubjectCreationDto() {
        SubjectCreationDto subjectCreationDto = new SubjectCreationDto();
        subjectCreationDto.setCode(code);
        subjectCreationDto.setName(name);
        subjectCreationDto.setCredits(credits);
        subjectCreationDto.setHours(hours);
        subjectCreationDto.setSemester(semester);
        subjectCreationDto.setRequiredCredits(requiredCredits);
        subjectCreationDto.setType(type);
        subjectCreationDto.setRequisites(requisites);
        subjectCreationDto.setGroups(new LinkedList<>(groups.values()));

        return subjectCreationDto;
    }
}
