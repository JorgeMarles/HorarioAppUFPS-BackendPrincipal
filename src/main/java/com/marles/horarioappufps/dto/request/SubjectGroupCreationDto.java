package com.marles.horarioappufps.dto.request;

import com.marles.horarioappufps.model.GroupState;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SubjectGroupCreationDto {
    private String code;
    private String teacher;
    private String program;
    private int maxCapacity;
    private int availableCapacity;
    private List<SessionCreationDto> sessions;
}
