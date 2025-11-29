package com.marles.horarioappufps.service;

import com.marles.horarioappufps.dto.response.PensumInfoDto;
import com.marles.horarioappufps.dto.response.SubjectInfoDto;
import com.marles.horarioappufps.model.Subject;
import com.marles.horarioappufps.model.User;
import com.marles.horarioappufps.repository.SubjectRepository;
import com.marles.horarioappufps.util.RequisiteValidator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
public class UserSubjectService {
    private final PensumService pensumService;
    private final UserService userService;

    @Autowired
    public UserSubjectService(PensumService pensumService, UserService userService) {
        this.pensumService = pensumService;
        this.userService = userService;
    }

    public void toggle(String uid, String code){
        User user = userService.getUserByUid(uid);
        Subject subject = pensumService.findByCode(code);
        if(user.containsSubject(subject)){
            deleteSubjectFromUser(user, subject.getCode());
        }else{
            addSubjectToUser(user, subject.getCode());
        }
    }

    private void addSubjectToUser(User user, String code) {
        addRecursively(code, user);
    }

    private void addRecursively(String code, User user) {
        Subject subject = pensumService.findByCode(code);
        user.addSubject(subject);
        for(Subject requisite : subject.getRequisites()) {
            if(!user.containsSubject(requisite)) {
                addRecursively(requisite.getCode(), user);
            }
        }
    }

    private void deleteSubjectFromUser(User user, String code) {
        deleteRecursively(code, user);
    }

    private void deleteRecursively(String code, User user) {
        Subject subject = pensumService.findByCode(code);
        user.removeSubject(subject);
        for(Subject unlock : pensumService.findUnlocks(subject)) {
            if(user.containsSubject(unlock)) {
                deleteRecursively(unlock.getCode(), user);
            }
        }
    }

    public void addList(String uid, List<String> codes) {
        List<Subject> subjects = codes.stream().map(pensumService::findByCode).toList();
        RequisiteValidator rv = new RequisiteValidator(pensumService.getPensum());
        rv.addAll(subjects);
        User user = userService.getUserByUid(uid);

        for(Subject subject : subjects) {
            this.addSubjectToUser(user, subject.getCode());
        }
    }
}
