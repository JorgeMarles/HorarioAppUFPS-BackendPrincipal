package com.marles.horarioappufps.dto.request;

import com.marles.horarioappufps.dto.jobs.BaseResponseData;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
public class PensumAsyncCreationDto extends BaseResponseData {
    private String name;
    private int semesters;
    private boolean updateTeachers;
    private Map<String, SubjectAsyncCreationDto> subjects = new HashMap<>();

    public PensumCreationDto toPensumCreationDto() {
        PensumCreationDto pensumCreationDto = new PensumCreationDto();

        pensumCreationDto.setName(name);
        pensumCreationDto.setSemesters(semesters);
        pensumCreationDto.setUpdateTeachers(updateTeachers);

        for(SubjectAsyncCreationDto subjectAsyncCreationDto : subjects.values()){
            pensumCreationDto.getSubjects().add(subjectAsyncCreationDto.toSubjectCreationDto());
        }

        return pensumCreationDto;
    }
}
