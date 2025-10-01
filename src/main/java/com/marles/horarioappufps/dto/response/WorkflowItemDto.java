package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.Workflow;
import com.marles.horarioappufps.model.WorkflowState;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class WorkflowItemDto {
    private String uuid;
    private Date startDate;
    private Date endDate;
    private WorkflowState state;

    public WorkflowItemDto(Workflow wf) {
        this.uuid = wf.getUuid().toString();
        this.startDate = wf.getStartDate();
        this.endDate = wf.getEndDate();
        this.state = wf.getState();
    }

    public static List<WorkflowItemDto> fromList(List<Workflow> list) {
        List<WorkflowItemDto> listDto = new LinkedList<>();
        for (Workflow wf : list) {
            listDto.add(new WorkflowItemDto(wf));
        }
        return listDto;
    }
}
