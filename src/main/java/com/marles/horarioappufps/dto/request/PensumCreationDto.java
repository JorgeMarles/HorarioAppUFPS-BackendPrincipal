package com.marles.horarioappufps.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PensumCreationDto {
    private Long id;
    private String name;
    private int semesters;
    private boolean updateTeachers;
    private List<SubjectCreationDto> subjects = new ArrayList<>();
}
