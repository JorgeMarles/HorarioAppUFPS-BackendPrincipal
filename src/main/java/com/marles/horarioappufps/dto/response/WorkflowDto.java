package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.Job;
import com.marles.horarioappufps.model.Workflow;
import com.marles.horarioappufps.model.WorkflowState;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class WorkflowDto {
    private String uuid;
    private Date startDate;
    private Date endDate;
    private WorkflowState state;
    private List<JobDto> jobs = new LinkedList<>();

    public WorkflowDto(Workflow wf) {
        this.uuid = wf.getUuid().toString();
        this.startDate = wf.getStartDate();
        this.endDate = wf.getEndDate();
        this.state = wf.getState();
        for(Job job : wf.getJobs()){
            this.jobs.add(new JobDto(job));
        }
    }
}
