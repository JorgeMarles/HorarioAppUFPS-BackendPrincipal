package com.marles.horarioappufps.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PensumAsyncRequestCreationDto {
    private String cookie;
    private boolean updateTeachers;
}
