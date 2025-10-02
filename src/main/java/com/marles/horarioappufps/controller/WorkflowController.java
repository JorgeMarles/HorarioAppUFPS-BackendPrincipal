package com.marles.horarioappufps.controller;

import com.marles.horarioappufps.dto.jobs.BaseResponseData;
import com.marles.horarioappufps.dto.jobs.JobResponse;
import com.marles.horarioappufps.dto.jobs.SubjectResponse;
import com.marles.horarioappufps.dto.request.PensumAsyncCreationDto;
import com.marles.horarioappufps.dto.request.PensumAsyncRequestCreationDto;
import com.marles.horarioappufps.dto.response.WorkflowDto;
import com.marles.horarioappufps.dto.response.WorkflowItemDto;
import com.marles.horarioappufps.model.Workflow;
import com.marles.horarioappufps.service.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workflow")
@Slf4j
public class WorkflowController {

    private final WorkflowService workflowService;

    @Autowired
    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WorkflowItemDto>> getAllWorkflows() {
        List<Workflow> list = workflowService.getAll();
        List<WorkflowItemDto> data = WorkflowItemDto.fromList(list);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkflowDto> getWorkflow(@PathVariable UUID uuid) {
        Workflow workflow = workflowService.getById(uuid);
        return ResponseEntity.ok(new WorkflowDto(workflow));
    }

    @PostMapping("/start")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UUID> startWorkflow(@RequestBody PensumAsyncRequestCreationDto dto) {
        UUID workflow = workflowService.startProcess(dto);
        return ResponseEntity.ok(workflow);
    }

    @PostMapping("/end/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UUID> endWorkflow(@PathVariable UUID uuid) {
        UUID workflow = workflowService.forceEnding(uuid);
        return ResponseEntity.ok(workflow);
    }

    @PostMapping("/job")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateJob(@RequestBody JobResponse<? extends BaseResponseData> rt) {
        BaseResponseData responseData = rt.getData();
        if(responseData instanceof PensumAsyncCreationDto pensumData) {
            JobResponse<PensumAsyncCreationDto> jobResponse = new JobResponse<>();
            jobResponse.setData(pensumData);
            jobResponse.setResponse(rt.getResponse());
            jobResponse.setJobId(rt.getJobId());
            jobResponse.setSuccess(rt.isSuccess());
            workflowService.endJob1(jobResponse);
        } else if(responseData instanceof SubjectResponse subjectData) {
            JobResponse<SubjectResponse> jobResponse = new JobResponse<>();
            jobResponse.setData(subjectData);
            jobResponse.setResponse(rt.getResponse());
            jobResponse.setJobId(rt.getJobId());
            jobResponse.setSuccess(rt.isSuccess());
            workflowService.endJob2Or3(jobResponse);
        }
        return ResponseEntity.ok("OK");
    }
}
