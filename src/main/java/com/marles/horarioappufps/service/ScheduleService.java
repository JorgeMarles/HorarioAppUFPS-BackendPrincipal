package com.marles.horarioappufps.service;

import com.marles.horarioappufps.dto.response.schedule.MessageType;
import com.marles.horarioappufps.dto.response.schedule.ScheduleGroupDto;
import com.marles.horarioappufps.dto.response.schedule.ScheduleGroupWrapper;
import com.marles.horarioappufps.dto.response.schedule.ScheduleInfoDto;
import com.marles.horarioappufps.exception.*;
import com.marles.horarioappufps.model.Schedule;
import com.marles.horarioappufps.model.Subject;
import com.marles.horarioappufps.model.SubjectGroup;
import com.marles.horarioappufps.model.User;
import com.marles.horarioappufps.repository.*;
import com.marles.horarioappufps.util.OverlapValidator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final PensumRepository pensumRepository;
    private final SubjectGroupRepository subjectGroupRepository;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository, UserRepository userRepository, SubjectRepository subjectRepository, PensumRepository pensumRepository, SubjectGroupRepository subjectGroupRepository) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.pensumRepository = pensumRepository;
        this.subjectGroupRepository = subjectGroupRepository;
    }

    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }

    public List<Schedule> getByUserUid(String uid) {
        return scheduleRepository.findByUser_Uid(uid);
    }

    public Schedule getById(Long id) {
        return scheduleRepository.findById(id).orElseThrow(() -> new ScheduleNotFoundException(id));
    }

    public ScheduleInfoDto getFromSchedule(Schedule schedule) {
        OverlapValidator overlapValidator = new OverlapValidator();
        List<ScheduleGroupWrapper> wrappers = schedule.getCodes().stream().map(code -> {
            try {
                SubjectGroup group = subjectGroupRepository.findByCode(code).orElseThrow(() -> new GroupNotFoundException(code));
                ScheduleGroupWrapper sgw = new ScheduleGroupWrapper(new ScheduleGroupDto(group));
                try {
                    overlapValidator.add(group);
                }catch (ScheduleConflictException e){
                    for(String err : e.getMessages()){
                        sgw.add(err, MessageType.ERROR);
                    }
                }
                return sgw;
            } catch (GroupNotFoundException e) {
                ScheduleGroupWrapper sgw = new ScheduleGroupWrapper(code);
                sgw.add("Grupo no encontrado o eliminado", MessageType.ERROR);
                return sgw;
            }
        }).toList();

        return new ScheduleInfoDto(schedule, wrappers);
    }

    public List<ScheduleInfoDto> getByUserUid_Dto(String uid){
        List<Schedule> data = this.getByUserUid(uid);
        List<ScheduleInfoDto> resp = new LinkedList<>();
        for(Schedule schedule : data){
            resp.add(getFromSchedule(schedule));
        }
        return resp;
    }

    public void validatePermissions(Long id, String uid, boolean isAdmin) {
        Schedule schedule = getById(id);
        if(!schedule.getUser().getUid().equals(uid) && !isAdmin) {
            throw new ScheduleNotFoundException(id);
        }
    }

    public ScheduleInfoDto getById_Dto(Long id) {
        Schedule schedule = getById(id);
        return getFromSchedule(schedule);
    }

    public Schedule createSchedule(String title, String uid) {
        Schedule schedule = new Schedule();
        User user = userRepository.findById(uid).orElseThrow(() -> new UserNotFoundException(uid));
        schedule.setUser(user);
        schedule.setPensum(pensumRepository.findById(1L).orElseThrow(() -> new PensumNotFoundException(1L)));
        schedule.setTitle(title);

        return scheduleRepository.save(schedule);
    }

    public Schedule duplicateSchedule(Long id, String uid){
        Schedule schedule = getById(id);
        User user = userRepository.findById(uid).orElseThrow(() -> new UserNotFoundException(uid));
        Schedule copy =  new Schedule();
        copy.setUser(user);
        copy.setTitle(schedule.getTitle());
        copy.setPensum(schedule.getPensum());
        copy.setCodes(schedule.getCodes().stream().map(e -> e).collect(Collectors.toSet()));

        return scheduleRepository.save(copy);
    }

    public void validateSubjectDuplicate(List<SubjectGroup> groups, Subject subject) {
        for(SubjectGroup group : groups) {
            if(group.getSubject().getCode().equals(subject.getCode())) {
                throw new ScheduleConflictException("Ya existe una materia con codigo " + subject.getCode() + " en el horario.");
            }
        }
    }

    /**
     * Tries to add the specified SubjectGroup to the specified Schedule
     * @param id The id of the Schedule
     * @param group The code of the group to be added
     * @return The updated Schedule
     * @throws ScheduleConflictException When it can't be added
     */
    public Schedule addSubjectGroup(Long id, String group) throws ScheduleConflictException {
        Schedule schedule = getById(id);
        SubjectGroup subjectGroup = subjectGroupRepository.findByCode(group).orElseThrow(() -> new GroupNotFoundException(group));
        List<SubjectGroup> groups = getFromList(schedule.getCodes());

        validateSubjectDuplicate(groups, subjectGroup.getSubject());

        OverlapValidator test = new OverlapValidator();
        test.addList(groups);
        test.add(subjectGroup);
        schedule.getCodes().add(subjectGroup.getCode());
        return scheduleRepository.save(schedule);
    }

    /**
     * Tries to add any SubjectGroup of the specified Subject to the specified Schedule
     * @param id the id of the Schedule
     * @param subjectCode the code of the Subject
     * @return The updated Schedule
     * @throws ScheduleConflictException if all the SubjectGroups of the specified Subject have any conflict with the actual Schedule
     */
    public Schedule addSubject(Long id, String subjectCode) throws ScheduleConflictException {
        Schedule schedule = getById(id);
        Subject subject = subjectRepository.findByCode(subjectCode).orElseThrow(() -> new SubjectNotFoundException(subjectCode));

        OverlapValidator test = new OverlapValidator();
        List<SubjectGroup> groups = getFromList(schedule.getCodes());
        validateSubjectDuplicate(groups, subject);
        test.addList(groups);

        Map<String, String> conflicts = new HashMap<>();

        boolean found = false;

        List<SubjectGroup> subjectGroups = subject.getGroups();
        subjectGroups.sort((a,b) -> {
            String c1 = a.getCode();
            String c2 = b.getCode();
            boolean sist1 = c1.startsWith("115");
            boolean sist2 = c2.startsWith("115");
            if(sist1 && !sist2) {
                return -1;
            }
            if(sist2 && !sist1) {
                return 1;
            }
            return c1.compareTo(c2);
        });

        for(SubjectGroup subjectGroup : subject.getGroups()) {
            SubjectGroup overlapped = test.overlaps(subjectGroup);
            if(overlapped != null) {
                conflicts.put(subjectGroup.getCode(), overlapped.getCode());
            } else {
                found = true;
                schedule.getCodes().add(subjectGroup.getCode());
                break;
            }
        }

        if(!found) {
            throw new ScheduleConflictException(conflicts);
        }

        return scheduleRepository.save(schedule);
    }

    public Schedule changeGroup(Long id, String oldCode, String newCode) throws ScheduleConflictException {
        Schedule schedule = getById(id);
        deleteFromSchedule(id, oldCode);
        addSubjectGroup(id, newCode);
        return scheduleRepository.save(schedule);
    }

    public Schedule deleteFromSchedule(Long id, String groupCode){
        Schedule schedule = getById(id);
        schedule.getCodes().remove(groupCode);
        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long id){
        Schedule schedule = getById(id);
        scheduleRepository.delete(schedule);
    }

    private List<SubjectGroup> getFromList(Set<String> codes) {
        return codes.stream().map(code -> {
            return subjectGroupRepository.findByCode(code).orElseThrow(() -> {
                return new GroupNotFoundException(code);
            });
        }).toList();
    }

}
