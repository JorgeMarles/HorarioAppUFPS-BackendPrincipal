package com.marles.horarioappufps.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CompletedSubjectsDto {
    private List<String> subjects;
}
