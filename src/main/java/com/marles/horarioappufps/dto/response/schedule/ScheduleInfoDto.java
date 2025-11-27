package com.marles.horarioappufps.dto.response.schedule;

import com.marles.horarioappufps.model.Schedule;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class ScheduleInfoDto {
    private Long id;
    private String title;
    private int credits;
    private List<ScheduleGroupWrapper> subjectGroups = new LinkedList<>();

    public ScheduleInfoDto(Schedule schedule, List<ScheduleGroupWrapper> subjectGroups) {
        this.id = schedule.getId();
        this.title = schedule.getTitle();
        this.credits = 0;
        for(ScheduleGroupWrapper subjectGroup : subjectGroups){
            this.subjectGroups.add(subjectGroup);
            this.credits += subjectGroup.getScheduleGroup().getCredits();
        }
    }
}
