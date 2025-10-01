package com.marles.horarioappufps.repository;

import com.marles.horarioappufps.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findAllByWorkflow_Uuid(UUID uuid);
}
