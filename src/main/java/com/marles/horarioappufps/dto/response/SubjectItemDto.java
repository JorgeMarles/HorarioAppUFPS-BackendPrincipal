package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.Subject;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubjectItemDto {
    private Long id;
    private String name;
    private String code;

    public SubjectItemDto(Subject subject) {
        this.id = subject.getId();
        this.name = subject.getName();
        this.code = subject.getCode();
    }
}
