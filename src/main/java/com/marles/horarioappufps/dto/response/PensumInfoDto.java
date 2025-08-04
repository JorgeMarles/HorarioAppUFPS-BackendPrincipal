package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.Pensum;
import com.marles.horarioappufps.model.Subject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class PensumInfoDto {
    private Long id;
    private String name;
    private int semesters;
    private List<SubjectInfoDto> subjects = new LinkedList<>();

    public PensumInfoDto(Pensum pensum) {
        this.id = pensum.getId();
        this.name = pensum.getName();
        this.semesters = pensum.getSemesters();

        for(Subject subject : pensum.getSubjects()) {
            this.subjects.add(new SubjectInfoDto(subject));
        }
    }
}
