package com.marles.horarioappufps.dto.request;

import com.marles.horarioappufps.model.SubjectType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SubjectCreationDto {
    private String code;
    private String name;
    private int credits;
    private int hours;
    private int semester;
    private int requiredCredits = 0;
    private SubjectType type;
    private List<SubjectGroupCreationDto> groups;
    private List<String> requisites;
}
