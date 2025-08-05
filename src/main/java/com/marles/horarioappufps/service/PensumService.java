package com.marles.horarioappufps.service;

import com.marles.horarioappufps.dto.request.PensumCreationDto;
import com.marles.horarioappufps.dto.request.SessionCreationDto;
import com.marles.horarioappufps.dto.request.SubjectCreationDto;
import com.marles.horarioappufps.dto.request.SubjectGroupCreationDto;
import com.marles.horarioappufps.exception.PensumNotFoundException;
import com.marles.horarioappufps.model.Pensum;
import com.marles.horarioappufps.model.Session;
import com.marles.horarioappufps.model.Subject;
import com.marles.horarioappufps.model.SubjectGroup;
import com.marles.horarioappufps.repository.PensumRepository;
import com.marles.horarioappufps.repository.SessionRepository;
import com.marles.horarioappufps.repository.SubjectGroupRepository;
import com.marles.horarioappufps.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
@Slf4j
public class PensumService {

    private final PensumRepository pensumRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectGroupRepository subjectGroupRepository;
    private final SessionRepository sessionRepository;

    private boolean updateTeachers;

    public Pensum getPensum(){
        return getPensum(1L);
    }

    public Pensum getPensum(Long id) {
        return pensumRepository.findById(id).orElseThrow(() -> new PensumNotFoundException(id));
    }

    @Autowired
    public PensumService(
            PensumRepository pensumRepository,
            SubjectRepository subjectRepository,
            SubjectGroupRepository subjectGroupRepository,
            SessionRepository sessionRepository
    ) {
        this.pensumRepository = pensumRepository;
        this.subjectRepository = subjectRepository;
        this.subjectGroupRepository = subjectGroupRepository;
        this.sessionRepository = sessionRepository;
    }

    public Pensum savePensum(PensumCreationDto pensumCreationDto) {
        Pensum pensum = new Pensum();
        if (pensumCreationDto.getId() != null) {
            pensum = pensumRepository.findById(pensumCreationDto.getId()).orElseThrow(() -> new PensumNotFoundException(pensumCreationDto.getId()));
        }

        updateFields(pensum, pensumCreationDto);

        this.updateTeachers = pensumCreationDto.isUpdateTeachers();

        pensum = pensumRepository.save(pensum);

        processSubjects(pensum, pensumCreationDto.getSubjects());

        return pensum;
    }

    private void processSubjects(Pensum pensum, List<SubjectCreationDto> subjectCreationDtos) {
        pensum.getSubjects().clear();

        Map<String, Subject> subjectMap = new HashMap<>();

        for (SubjectCreationDto subjectCreationDto : subjectCreationDtos) {
            Subject subject = createOrUpdateSubject(subjectCreationDto, pensum);
            subjectMap.put(subject.getCode(), subject);
            pensum.getSubjects().add(subject);
        }

        for (int i = 0; i < subjectCreationDtos.size(); i++) {
            SubjectCreationDto subjectCreationDto = subjectCreationDtos.get(i);
            Subject subject = pensum.getSubjects().get(i);
            processGroups(subject, subjectCreationDto.getGroups());
        }

        for (int i = 0; i < subjectCreationDtos.size(); i++) {
            SubjectCreationDto subjectCreationDto = subjectCreationDtos.get(i);
            Subject subject = pensum.getSubjects().get(i);
            processRequisite(subject, subjectCreationDto.getRequisites(), subjectMap);
        }
    }

    private void processRequisite(Subject subject, List<String> requisites, Map<String, Subject> subjectMap) {
        subject.getRequisites().clear();
        for (String requisite : requisites) {
            Subject subjectRequisite = subjectMap.get(requisite);
            if (subjectRequisite != null) {
                subject.getRequisites().add(subjectRequisite);
            }
        }
        subjectRepository.save(subject);
    }

    private Subject createOrUpdateSubject(SubjectCreationDto subjectCreationDto, Pensum pensum) {
        Subject subject = new Subject();
        Optional<Subject> subjectOpt = subjectRepository.findByCode(subjectCreationDto.getCode());
        if (subjectOpt.isPresent()) {
            subject = subjectOpt.get();
        }

        updateFields(subject, subjectCreationDto);
        subject.setPensum(pensum);

        return subjectRepository.save(subject);
    }

    private void processGroups(Subject subject, List<SubjectGroupCreationDto> subjectGroupCreationDtos) {
        subject.getGroups().clear();

        for (SubjectGroupCreationDto subjectGroupCreationDto : subjectGroupCreationDtos) {
            SubjectGroup subjectGroup = createOrUpdateSubjectGroup(subjectGroupCreationDto, subject);
            subject.getGroups().add(subjectGroup);

            processSessions(subjectGroup, subjectGroupCreationDto.getSessions());
        }
    }

    private void processSessions(SubjectGroup subjectGroup, List<SessionCreationDto> sessionCreationDtos) {
        subjectGroup.getSessions().clear();
        for (SessionCreationDto sessionCreationDto : sessionCreationDtos) {
            Session session = createSession(sessionCreationDto, subjectGroup);
            subjectGroup.getSessions().add(session);
        }
    }

    public Session createSession(SessionCreationDto sessionCreationDto, SubjectGroup subjectGroup) {
        Session session = new Session();

        updateFields(session, sessionCreationDto);
        session.setGroup(subjectGroup);

        return sessionRepository.save(session);
    }

    public SubjectGroup createOrUpdateSubjectGroup(SubjectGroupCreationDto subjectGroupCreationDto, Subject subject) {
        SubjectGroup subjectGroup = new SubjectGroup();
        Optional<SubjectGroup> subjectGroupOpt = subjectGroupRepository.findByCode(subjectGroupCreationDto.getName());
        if (subjectGroupOpt.isPresent()) {
            subjectGroup = subjectGroupOpt.get();
        }

        updateFields(subjectGroup, subjectGroupCreationDto);
        subjectGroup.setSubject(subject);

        return subjectGroupRepository.save(subjectGroup);
    }

    private void updateFields(Pensum pensum, PensumCreationDto pensumCreationDto) {
        pensum.setName(pensumCreationDto.getName());
        pensum.setSemesters(pensumCreationDto.getSemesters());
        pensum.setLastModified(new Date());
    }

    private void updateFields(Subject subject, SubjectCreationDto subjectCreationDto) {
        subject.setName(subjectCreationDto.getName());
        subject.setCode(subjectCreationDto.getCode());
        subject.setCredits(subjectCreationDto.getCredits());
        subject.setHours(subjectCreationDto.getHours());
        subject.setRequiredCredits(subjectCreationDto.getRequiredCredits());
        subject.setSemester(subjectCreationDto.getSemester());
        subject.setType(subjectCreationDto.getType());
    }

    private void updateFields(SubjectGroup subjectGroup, SubjectGroupCreationDto subjectGroupCreationDto) {
        subjectGroup.setCode(subjectGroupCreationDto.getName());
        if(subjectGroupCreationDto.getProgram() != null) {
            subjectGroup.setProgram(subjectGroupCreationDto.getProgram());
        } else {
            subjectGroup.setProgram(subjectGroupCreationDto.getName().substring(0,3));
        }
        subjectGroup.setAvailableCapacity(subjectGroupCreationDto.getAvailableCapacity());
        subjectGroup.setMaxCapacity(subjectGroupCreationDto.getMaxCapacity());
        if(subjectGroup.getTeacher() == null) {
            subjectGroup.setTeacher(subjectGroupCreationDto.getTeacher());
            subjectGroup.setCurrentTeacher(true);
        } else {
            if(!"-".equals(subjectGroupCreationDto.getTeacher())) {
                subjectGroup.setTeacher(subjectGroupCreationDto.getTeacher());
                subjectGroup.setCurrentTeacher(true);
            } else if("-".equals(subjectGroup.getTeacher())) {
                subjectGroup.setCurrentTeacher(true);
            }
        }
    }

    private void updateFields(Session session, SessionCreationDto sessionCreationDto) {
        session.setClassroom(sessionCreationDto.getClassroom());
        session.setDay(sessionCreationDto.getDay());
        session.setBeginHour(sessionCreationDto.getBeginHour());
        session.setEndHour(sessionCreationDto.getEndHour());
    }

}
