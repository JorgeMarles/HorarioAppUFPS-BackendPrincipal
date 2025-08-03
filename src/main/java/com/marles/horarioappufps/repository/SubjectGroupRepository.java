package com.marles.horarioappufps.repository;

import com.marles.horarioappufps.model.SubjectGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectGroupRepository extends JpaRepository<SubjectGroup, Long> {
    Optional<SubjectGroup> findByCode(String code);
}
