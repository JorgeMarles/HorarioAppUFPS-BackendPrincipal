package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.Schedule;
import com.marles.horarioappufps.model.SubjectGroup;
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
    private List<GroupScheduleDto> subjectGroups = new LinkedList<>();

    public ScheduleInfoDto(Schedule schedule, List<SubjectGroup> subjectGroups) {
        this.id = schedule.getId();
        this.title = schedule.getTitle();
        this.credits = 0;
        for(SubjectGroup subjectGroup : subjectGroups){
            this.subjectGroups.add(new GroupScheduleDto(subjectGroup));
            this.credits += subjectGroup.getSubject().getCredits();
        }
    }
}
