package com.marles.horarioappufps.util;

import com.marles.horarioappufps.exception.RequisiteConflictException;
import com.marles.horarioappufps.exception.SubjectNotFoundException;
import com.marles.horarioappufps.model.Pensum;
import com.marles.horarioappufps.model.Subject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Data
public class RequisiteValidator {
    private Map<String, Subject> pensum;
    private Map<String, String> visited = new HashMap<>();
    private Set<String> subjects = new HashSet<>();

    public RequisiteValidator(Pensum pensum) {
        this.pensum = new HashMap<>();
        for (Subject subject : pensum.getSubjects()) {
            this.pensum.put(subject.getCode(), subject);
        }
    }

    /**
     * Mark a Node as visited
     *
     * @param subject the subject marking as visited
     * @param origin the origin of this DFS
     * @return true when this was the first visit of the node
     * @throws RequisiteConflictException when the subject is par of the registered subjects
     */
    private boolean markAsVisited(Subject subject, Subject origin) throws RequisiteConflictException {
        if (subjects.contains(subject.getCode())) {
            throw new RequisiteConflictException("La materia " + subject.getCode() + " es prerrequisito de " + origin.getCode());
        }
        boolean firstTime = !visited.containsKey(subject.getCode());
        visited.put(subject.getCode(), origin.getCode());
        return firstTime;
    }

    private void addRecursively(Subject subject, Subject origin) throws RequisiteConflictException {
        boolean firstTime = markAsVisited(subject, origin);
        if (!firstTime) {
            return;
        }
        for (Subject requisite : subject.getRequisites()) {
            addRecursively(requisite, origin);
        }
    }

    public void add(String code) throws RequisiteConflictException {
        if (subjects.contains(code)) {
            throw new RequisiteConflictException("La materia de codigo " + code + " ya está en este horario");
        }
        if (visited.containsKey(code)) {
            throw new RequisiteConflictException("La materia con codigo " + code + " es prerrequisito de " + visited.get(code));
        }
        Subject subject = this.pensum.get(code);
        if (subject == null) {
            throw new SubjectNotFoundException(code);
        }
        addRecursively(subject, subject);
        subjects.add(code);
    }

    public void add(Subject subject) throws RequisiteConflictException {
        this.add(subject.getCode());
    }

    public void addAll(List<Subject> subjects) throws RequisiteConflictException {
        for (Subject subject : subjects) {
            this.add(subject);
        }
    }
}
