package com.marles.horarioappufps.service;

import com.marles.horarioappufps.dto.jobs.JobResponse;
import com.marles.horarioappufps.dto.jobs.PensumRequest;
import com.marles.horarioappufps.dto.jobs.SubjectRequest;
import com.marles.horarioappufps.dto.jobs.SubjectResponse;
import com.marles.horarioappufps.dto.request.*;
import com.marles.horarioappufps.exception.JobNotFoundException;
import com.marles.horarioappufps.exception.ScrappingException;
import com.marles.horarioappufps.exception.ServiceBusyException;
import com.marles.horarioappufps.exception.WorkflowNotFoundException;
import com.marles.horarioappufps.model.*;
import com.marles.horarioappufps.repository.JobRepository;
import com.marles.horarioappufps.repository.WorkflowRepository;
import com.marles.horarioappufps.util.DataSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    private final JobRepository jobRepository;

    private final PensumService pensumService;

    private final DataSender dataSender;

    private final AtomicBoolean available = new AtomicBoolean(true);
    private PensumAsyncCreationDto pacdto = null;
    private int jobsUnprocessed = 0;
    private int jobNumber = 1;
    private String currentCookie = null;


    @Autowired
    public WorkflowService(
            WorkflowRepository workflowRepository,
            JobRepository jobRepository,
            PensumService pensumService,
            DataSender dataSender) {
        this.workflowRepository = workflowRepository;
        this.jobRepository = jobRepository;
        this.pensumService = pensumService;
        this.dataSender = dataSender;
    }

    public List<Workflow> getAll() {
        return workflowRepository.findAll();
    }

    public Workflow getById(UUID id) {
        return workflowRepository.findById(id).orElseThrow(() -> new WorkflowNotFoundException(id));
    }

    public void start() {
        pacdto = new PensumAsyncCreationDto();
        jobNumber = 1;
        currentCookie = null;
    }

    public Job getJob(Long id) {
        return jobRepository.findById(id).orElseThrow(() -> new JobNotFoundException(id));
    }

    public void resetVariables(){
        pacdto = null;
        jobsUnprocessed = 0;
        jobNumber = 1;
        currentCookie = null;
    }

    public void end(Job finalJob, boolean success) {
        if (success) {
            PensumCreationDto pensumCreationDto = pacdto.toPensumCreationDto();
            //log.info(pensumCreationDto.toString());
            pensumService.savePensum(pensumCreationDto);
        }
        Workflow workflow = finalJob.getWorkflow();
        workflow.setEndDate(new Date());
        workflow.setState(success ? WorkflowState.SUCCESS : WorkflowState.ERROR);
        workflow.getJobs().stream().filter((job) -> job.getState() == JobState.PENDING).forEach((job) -> {
            job.setState(JobState.ERROR);
            jobRepository.save(job);
        });
        workflowRepository.save(workflow);
        resetVariables();
    }

    public UUID startProcess(PensumAsyncRequestCreationDto dto) {
        if (!available.compareAndSet(true, false)) {
            throw new ServiceBusyException("El servidor ya está procesando una solicitud");
        }
        start();
        currentCookie = dto.getCookie();

        pacdto.setUpdateTeachers(dto.isUpdateTeachers());
        Workflow workflow = new Workflow();
        workflow.setState(WorkflowState.PROCESSING);
        workflow.setStartDate(new Date());
        workflow = workflowRepository.save(workflow);
        log.info("Starting job");
        startJob1(workflow);
        return workflowRepository.save(workflow).getUuid();
    }

    public UUID forceEnding(UUID uuid) {
        Workflow workflow = getById(uuid);
        workflow.setState(WorkflowState.STOPPED);
        workflow.setEndDate(new Date());
        resetVariables();
        return workflowRepository.save(workflow).getUuid();
    }

    public Job endJob(Long jobId, boolean success, String response, String description) {
        Job job = getJob(jobId);
        if(job.getState() != JobState.PENDING){
            throw new ScrappingException("Este trabajo ya fué procesado");
        }
        if (!success) {
            log.error("Error: job {} unsuccessful: {}", job, description);
            end(job, false);
            job.setResponse(response);
        } else {
            job.setState(JobState.SUCCESS);
            job.setResponse(response);
            job.setDescription(description);
        }
        return jobRepository.save(job);
    }

    public Job createStartingJob(String description, JobType jobType, Workflow workflow) {
        Job job = new Job();
        job.setState(JobState.PENDING);
        job.setDescription(description);
        job.setNumber(jobNumber++);
        job.setType(jobType);
        workflow.addJob(job);
        this.jobsUnprocessed++;
        return jobRepository.save(job);
    }

    public void startJob1(Workflow workflow) {
        Job job = createStartingJob("Consultando Información del Pensum", JobType.PENSUM_INFO, workflow);
        //Send(cookie)
        dataSender.send(new PensumRequest(job.getId(), currentCookie));
    }

    /**
     * When this function is called, it should have a PensumAsyncCreationDto with the following:
     * - name
     * - semesters
     * - A HashMap of < SubjectCode(string), SubjectAsyncCreationDto>
     * for each SubjectAsyncCreationDto, it should have all the info except for groups
     */
    public void endJob1(JobResponse<PensumAsyncCreationDto> response) {
        Job job = endJob(response.getJobId(), response.isSuccess(), response.getResponse(), "Pensum "+response.getData().getName()+" procesado correctamente");
        Workflow workflow = job.getWorkflow();
        Map<String, SubjectAsyncCreationDto> subjects = response.getData().getSubjects();
        for (SubjectAsyncCreationDto dto : subjects.values()) {
            this.pacdto.getSubjects().put(dto.getCode(), dto);
            startJob2(workflow, dto);
        }
        this.jobsUnprocessed--;
    }

    public void startJob2(Workflow workflow, SubjectAsyncCreationDto dto) {
        Job job = createStartingJob("Consultando materia " + dto.getName() + " - " + dto.getCode(), JobType.SUBJECT_INFO, workflow);
        //send(cookie, subjectCode, true)
        dataSender.send(new SubjectRequest(job.getId(), currentCookie, dto.getCode(), true));
    }

    public void startJob3(Workflow workflow, SubjectAsyncCreationDto dto, String code) {
        Job job = createStartingJob("Consultando equivalencia " + dto.getName() + " - " + code, JobType.EQUIVALENCE_INFO, workflow);
        //send(cookie, subjectCode, false)
        dataSender.send(new SubjectRequest(job.getId(), currentCookie, dto.getCode(), false));
    }

    public void endJob2Or3(JobResponse<SubjectResponse> response) {
        Job job = endJob(response.getJobId(), response.isSuccess(), response.getResponse(), "Materia "+response.getData().getCode()+" procesada correctamente");
        Workflow workflow = job.getWorkflow();
        String code = response.getData().getCode();
        SubjectAsyncCreationDto subject = this.pacdto.getSubjects().get(code);
        if (subject == null) {
            throw new ScrappingException("Could not find " + code + " in current temporal object");
        }
        Map<String, SubjectGroupCreationDto> groups = response.getData().getGroups();
        subject.getGroups().putAll(groups);
        if (job.getType() == JobType.SUBJECT_INFO) {
            for (String equivalenceCode : subject.getEquivalences()) {
                startJob3(workflow, subject, equivalenceCode);
            }
        }
        this.jobsUnprocessed--;
        log.info("Job {} ended, {} remaining", job,  jobsUnprocessed);
        if (this.jobsUnprocessed == 0) {
            end(job, true);
        }
    }
}
