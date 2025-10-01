package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.Job;
import com.marles.horarioappufps.model.JobState;
import com.marles.horarioappufps.model.JobType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobDto {
    private Long id;
    private int number;
    private JobType type;
    private JobState state;
    private String response;
    private String description;

    public JobDto(Job job){
        this.id = job.getId();
        this.number = job.getNumber();
        this.type = job.getType();
        this.state = job.getState();
        this.response = job.getResponse();
        this.description = job.getDescription();
    }
}
