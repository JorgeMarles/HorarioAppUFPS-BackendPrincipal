package com.marles.horarioappufps.util;

import com.marles.horarioappufps.exception.RequisiteConflictException;
import com.marles.horarioappufps.model.Pensum;
import com.marles.horarioappufps.model.Subject;
import com.marles.horarioappufps.service.PensumService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class RequisiteValidator {
    private Pensum pensum;
    private Map<String, String> visited = new HashMap<>();
    private Set<String> subjects = new HashSet<>();

    public RequisiteValidator(@Autowired PensumService pensumService) {
        this.pensum = pensumService.getPensum();
    }

    private void markAsVisited(Subject subject, Subject origin) throws RequisiteConflictException {
        if (subjects.contains(subject.getCode())) {
            throw new RequisiteConflictException("La materia " + subject.getCode() + " es prerrequisito de " + origin.getCode());
        }
        visited.put(subject.getCode(), origin.getCode());
    }

    private void addRecursively(Subject subject, Subject origin) throws RequisiteConflictException {
        markAsVisited(subject, origin);
        for (Subject requisite : subject.getRequisites()) {
            if (!visited.containsKey(requisite.getCode())) {
                addRecursively(requisite, origin);
            }
        }
    }

    public void add(Subject subject) throws RequisiteConflictException {
        if (subjects.contains(subject.getCode())) {
            throw new RequisiteConflictException("La materia de codigo " + subject.getCode() + " ya está en este horario");
        }
        if (visited.containsKey(subject.getCode())) {
            throw new RequisiteConflictException("La materia con codigo " + subject.getCode() + " es prerrequisito de " + visited.get(subject.getCode()));
        }
        addRecursively(subject, subject);
        subjects.add(subject.getCode());
    }
}
