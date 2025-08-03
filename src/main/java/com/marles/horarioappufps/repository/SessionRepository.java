package com.marles.horarioappufps.repository;

import com.marles.horarioappufps.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
}
