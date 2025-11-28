package com.marles.horarioappufps.service;

import com.marles.horarioappufps.model.Subject;
import com.marles.horarioappufps.model.User;
import com.marles.horarioappufps.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Transactional
@Slf4j
public class UserSubjectService {
    private final PensumService pensumService;
    private final UserService userService;
    private final SubjectRepository subjectRepository;

    @Autowired
    public UserSubjectService(PensumService pensumService, UserService userService, SubjectRepository subjectRepository) {
        this.pensumService = pensumService;
        this.userService = userService;
        this.subjectRepository = subjectRepository;
    }

    public void addSubjectToUser(String uid, String code) {
        User user = userService.getUserByUid(uid);
        addRecursively(code, user);
    }

    private void addRecursively(String code, User user) {
        Subject subject = pensumService.findByCode(code);
        user.addSubject(subject);
        for(Subject requisite : subject.getRequisites()) {
            if(!user.containsSubject(requisite)) {
                addRecursively(subject.getCode(), user);
            }
        }
    }

    public void deleteSubjectFromUser(String uid, String code) {
        User user = userService.getUserByUid(uid);
        deleteRecursively(code, user);
    }

    private void deleteRecursively(String code, User user) {
        Subject subject = pensumService.findByCode(code);
        user.removeSubject(subject);
        for(Subject requisite : pensumService.findUnlocks(subject)) {
            if(user.containsSubject(requisite)) {
                deleteRecursively(subject.getCode(), user);
            }
        }
    }
}
