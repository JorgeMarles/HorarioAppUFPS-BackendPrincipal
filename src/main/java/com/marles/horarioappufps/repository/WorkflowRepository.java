package com.marles.horarioappufps.repository;

import com.marles.horarioappufps.model.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkflowRepository extends JpaRepository<Workflow, UUID> {
}
