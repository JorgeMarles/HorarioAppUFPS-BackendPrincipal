package com.marles.horarioappufps.repository;

import com.marles.horarioappufps.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByCode(String code);
    List<Subject> findAllByNameContaining(String name);
    List<Subject> findDistinctByGroups_TeacherContainingIgnoreCase(String teacher);
}
