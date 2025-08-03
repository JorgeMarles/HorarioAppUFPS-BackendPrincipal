package com.marles.horarioappufps.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SessionCreationDto {
    private int day;
    private int hour;
    private String classroom;
}
