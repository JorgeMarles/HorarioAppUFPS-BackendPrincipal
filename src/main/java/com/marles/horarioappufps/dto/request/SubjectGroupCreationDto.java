package com.marles.horarioappufps.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SubjectGroupCreationDto {
    private String code;
    private String teacher;
    private int maxCapacity;
    private int availableCapacity;
    private List<SessionCreationDto> sessions;
}
