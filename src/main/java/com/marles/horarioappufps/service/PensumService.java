package com.marles.horarioappufps.service;

import com.marles.horarioappufps.dto.request.PensumCreationDto;
import com.marles.horarioappufps.dto.request.SessionCreationDto;
import com.marles.horarioappufps.dto.request.SubjectCreationDto;
import com.marles.horarioappufps.dto.request.SubjectGroupCreationDto;
import com.marles.horarioappufps.dto.response.PensumInfoDto;
import com.marles.horarioappufps.dto.response.SubjectItemDto;
import com.marles.horarioappufps.exception.PensumNotFoundException;
import com.marles.horarioappufps.exception.ScheduleConflictException;
import com.marles.horarioappufps.exception.SubjectNotFoundException;
import com.marles.horarioappufps.model.*;
import com.marles.horarioappufps.repository.PensumRepository;
import com.marles.horarioappufps.repository.SessionRepository;
import com.marles.horarioappufps.repository.SubjectGroupRepository;
import com.marles.horarioappufps.repository.SubjectRepository;
import com.marles.horarioappufps.util.OverlapValidator;
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
    private final UserService userService;

    @Autowired
    public PensumService(
            PensumRepository pensumRepository,
            SubjectRepository subjectRepository,
            SubjectGroupRepository subjectGroupRepository,
            SessionRepository sessionRepository,
            UserService userService
    ) {
        this.pensumRepository = pensumRepository;
        this.subjectRepository = subjectRepository;
        this.subjectGroupRepository = subjectGroupRepository;
        this.sessionRepository = sessionRepository;
        this.userService = userService;
    }

    public Pensum getPensum() {
        return getOrCreatePensum(1L);
    }

    public PensumInfoDto getPensumInfoDto(String uid) {
        Pensum pensum = getPensum();
        User user = userService.getUserByUid(uid);
        PensumInfoDto pensumInfoDto = new PensumInfoDto(pensum, user);

        pensumInfoDto.sortByDepth();
        return pensumInfoDto;
    }

    public Pensum getPensum(Long id) {
        return pensumRepository.findById(id).orElseThrow(() -> new PensumNotFoundException(id));
    }

    public Pensum getOrCreatePensum(Long id) {
        Pensum pensum = pensumRepository.findById(id).orElseGet(() -> {
            Pensum p = new Pensum();
            p.setName("Pensum");
            return p;
        });
        return pensumRepository.save(pensum);
    }

    @Transactional
    public Pensum savePensum(PensumCreationDto pensumCreationDto) {
        if(pensumCreationDto.getId() == null){
            pensumCreationDto.setId(1L);
        }
        Pensum pensum = getOrCreatePensum(pensumCreationDto.getId());

        updateFields(pensum, pensumCreationDto);

        pensum = pensumRepository.save(pensum);

        processSubjects(pensum, pensumCreationDto.getSubjects(), pensumCreationDto.isUpdateTeachers());

        return pensum;
    }

    public Subject findByCode(String code) {
        return subjectRepository.findByCode(code).orElseThrow(() -> new SubjectNotFoundException(code));
    }

    public Subject findByCodeOrNull(String code) {
        return subjectRepository.findByCode(code).orElse(null);
    }

    public Set<Subject> findUnlocks(Subject subject){
        return subjectRepository.findDistinctByRequisites_Id(subject.getId());
    }

    private void processSubjects(Pensum pensum, List<SubjectCreationDto> subjectCreationDtos, boolean updateTeachers) {
        Map<String, Subject> subjectMap = new HashMap<>();
        List<Subject> newSubjects = new ArrayList<>();

        for (SubjectCreationDto subjectCreationDto : subjectCreationDtos) {
            if (subjectMap.containsKey(subjectCreationDto.getCode())) {
                throw new IllegalArgumentException("Código " + subjectCreationDto.getCode() + " duplicado al crear materia");
            }
            Subject subject = createOrUpdateSubject(subjectCreationDto, pensum);
            subjectMap.put(subject.getCode(), subject);
            newSubjects.add(subject);
        }

        for (SubjectCreationDto subjectCreationDto : subjectCreationDtos) {
            for (SubjectItemDto requisite : subjectCreationDto.getRequisites()) {
                Subject subjectReq = subjectMap.get(requisite.getCode());
                if (subjectReq == null) {
                    continue;
                }
                if (subjectReq.getSemester() >= subjectCreationDto.getSemester()) {
                    throw new IllegalArgumentException("El prerequisito " + requisite.getCode() + " no es válido para la materia " + subjectCreationDto.getCode() + " por semestre (" + subjectReq.getSemester() + " >= " + subjectCreationDto.getSemester() + ")");
                }
            }
        }

        Set<String> groupSet = new HashSet<>();
        for (int i = 0; i < subjectCreationDtos.size(); i++) {
            SubjectCreationDto subjectCreationDto = subjectCreationDtos.get(i);
            for (SubjectGroupCreationDto group : subjectCreationDto.getGroups()) {
                if (groupSet.contains(group.getCode())) {
                    throw new IllegalArgumentException("Código " + group.getCode() + " duplicado al crear grupo, materia "+subjectCreationDto.getCode());
                }
                groupSet.add(group.getCode());
            }
            Subject subject = newSubjects.get(i);
            processGroups(subject, subjectCreationDto.getGroups(), updateTeachers);
        }

        for (int i = 0; i < subjectCreationDtos.size(); i++) {
            SubjectCreationDto subjectCreationDto = subjectCreationDtos.get(i);
            Subject subject = newSubjects.get(i);
            processRequisite(subject, subjectCreationDto.getRequisites(), subjectMap);
        }

        pensum.getSubjects().clear();
        pensum.getSubjects().addAll(newSubjects);
    }

    private void validateOverlappingForSubjectGroup(SubjectGroup group){
        OverlapValidator ov = new OverlapValidator();
        ov.add(group);
    }

    private void processRequisite(Subject subject, List<SubjectItemDto> requisites, Map<String, Subject> subjectMap) {
        subject.getRequisites().clear();
        for (SubjectItemDto requisite : requisites) {
            Subject subjectRequisite = subjectMap.get(requisite.getCode());
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

    private void processGroups(Subject subject, List<SubjectGroupCreationDto> subjectGroupCreationDtos, boolean updateTeachers) {
        List<SubjectGroup> newSubjectGroups = new ArrayList<>();

        for (SubjectGroupCreationDto subjectGroupCreationDto : subjectGroupCreationDtos) {
            SubjectGroup subjectGroup = createOrUpdateSubjectGroup(subjectGroupCreationDto, subject, updateTeachers);
            subject.getGroups().add(subjectGroup);

            processSessions(subjectGroup, subjectGroupCreationDto.getSessions());

            try {
                validateOverlappingForSubjectGroup(subjectGroup);
            }catch(ScheduleConflictException ex){
                throw new IllegalArgumentException("Solapamiento en materia: "+ex.getMessage());
            }

            newSubjectGroups.add(subjectGroup);
        }

        subject.getGroups().clear();
        subject.getGroups().addAll(newSubjectGroups);
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

    public SubjectGroup createOrUpdateSubjectGroup(SubjectGroupCreationDto subjectGroupCreationDto, Subject subject, boolean updateTeachers) {
        SubjectGroup subjectGroup = new SubjectGroup();
        Optional<SubjectGroup> subjectGroupOpt = subjectGroupRepository.findByCode(subjectGroupCreationDto.getCode());
        if (subjectGroupOpt.isPresent()) {
            subjectGroup = subjectGroupOpt.get();
        }

        updateFields(subjectGroup, subjectGroupCreationDto, updateTeachers);
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

    private void updateFields(SubjectGroup subjectGroup, SubjectGroupCreationDto subjectGroupCreationDto, boolean updateTeachers) {
        subjectGroup.setCode(subjectGroupCreationDto.getCode());
        subjectGroup.setProgram(subjectGroupCreationDto.getCode().substring(0, 3));
        subjectGroup.setAvailableCapacity(subjectGroupCreationDto.getAvailableCapacity());
        subjectGroup.setMaxCapacity(subjectGroupCreationDto.getMaxCapacity());
        if (subjectGroup.getTeacher() == null) {
            subjectGroup.setTeacher("-");
        }
        if (!updateTeachers) {
            return;
        }
        if (subjectGroup.getTeacher() == null) {
            subjectGroup.setTeacher(subjectGroupCreationDto.getTeacher());
            subjectGroup.setCurrentTeacher(true);
        } else {
            if (!"-".equals(subjectGroupCreationDto.getTeacher())) {
                subjectGroup.setTeacher(subjectGroupCreationDto.getTeacher());
                subjectGroup.setCurrentTeacher(true);
            } else if ("-".equals(subjectGroup.getTeacher())) {
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
