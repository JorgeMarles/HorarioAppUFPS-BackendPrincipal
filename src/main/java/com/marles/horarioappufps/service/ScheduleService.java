package com.marles.horarioappufps.service;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Schedule createSchedule(String uid, String title) {
        Schedule schedule = new Schedule();
        User user = userRepository.findById(uid).orElseThrow(() -> new UserNotFoundException(uid));
        schedule.setUser(user);
        schedule.setPensum(pensumRepository.findById(1L).orElseThrow(() -> new PensumNotFoundException(1L)));
        schedule.setTitle(title);

        return scheduleRepository.save(schedule);
    }

    public Schedule getById(Long id, String uid) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new ScheduleNotFoundException(id));
        if (!schedule.getUser().getUid().equals(uid)) {
            throw new ScheduleNotFoundException(id);
        }
        return schedule;
    }

    /**
     * Tries to add the specified SubjectGroup to the specified Schedule
     * @param id The id of the Schedule
     * @param uid The User's uid for validation
     * @param group The code of the group to be added
     * @return The updated Schedule
     * @throws ScheduleConflictException When it can't be added
     */
    public Schedule addSubjectGroup(Long id, String uid, String group) throws ScheduleConflictException {
        Schedule schedule = getById(id, uid);
        SubjectGroup subjectGroup = subjectGroupRepository.findByCode(group).orElseThrow(() -> new GroupNotFoundException(group));
        OverlapValidator test = new OverlapValidator();
        List<SubjectGroup> groups = getFromList(schedule.getCodes());
        test.addList(groups);
        test.add(subjectGroup);
        schedule.getCodes().add(subjectGroup.getCode());
        return scheduleRepository.save(schedule);
    }

    /**
     * Tries to add any SubjectGroup of the specified Subject to the specified Schedule
     * @param id the id of the Schedule
     * @param uid the User's uid for validation
     * @param subjectCode the code of the Subject
     * @return The updated Schedule
     * @throws ScheduleConflictException if all the SubjectGroups of the specified Subject have any conflict with the actual Schedule
     */
    public Schedule addSubject(Long id, String uid, String subjectCode) throws ScheduleConflictException {
        Schedule schedule = getById(id, uid);
        Subject subject = subjectRepository.findByCode(subjectCode).orElseThrow(() -> new SubjectNotFoundException(subjectCode));

        OverlapValidator test = new OverlapValidator();
        List<SubjectGroup> groups = getFromList(schedule.getCodes());
        test.addList(groups);

        Map<String, String> conflicts = new HashMap<>();

        boolean found = false;

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

    private List<SubjectGroup> getFromList(List<String> codes) {
        return codes.stream().map(code -> {
            return subjectGroupRepository.findByCode(code).orElseThrow(() -> {
                return new GroupNotFoundException(code);
            });
        }).toList();
    }

}
