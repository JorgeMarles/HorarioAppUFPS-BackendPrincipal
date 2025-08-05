package com.marles.horarioappufps.repository;

import com.marles.horarioappufps.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUser_Uid(String uid);
}
