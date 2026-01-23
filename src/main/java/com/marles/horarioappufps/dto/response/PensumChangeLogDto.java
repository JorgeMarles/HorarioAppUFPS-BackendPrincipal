package com.marles.horarioappufps.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PensumChangeLogDto {
    private Long id;
    private Date date;
    private Long pensumId;
    private String pensumName;
    private List<SubjectChangeLogDto> subjectChangeLogs;
}
