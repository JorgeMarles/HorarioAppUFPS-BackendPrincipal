package com.marles.horarioappufps.service;

import com.marles.horarioappufps.model.GroupChangeLog;
import com.marles.horarioappufps.model.Pensum;
import com.marles.horarioappufps.model.PensumChangeLog;
import com.marles.horarioappufps.repository.GroupChangeLogRepository;
import com.marles.horarioappufps.repository.PensumChangeLogRepository;
import com.marles.horarioappufps.repository.PensumRepository;
import com.marles.horarioappufps.repository.SubjectChangeLogRepository;
import com.marles.horarioappufps.util.PensumChangeDetector;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PensumChangeService {

    private final PensumChangeLogRepository pensumChangeLogRepository;
    private final PensumRepository pensumRepository;

    @Autowired
    public PensumChangeService(
            PensumChangeLogRepository pensumChangeLogRepository,
            SubjectChangeLogRepository subjectChangeLogRepository,
            GroupChangeLogRepository groupChangeLogRepository,
            PensumRepository pensumRepository) {
        this.pensumChangeLogRepository = pensumChangeLogRepository;
        this.pensumRepository = pensumRepository;
    }

    public void registerChanges(Pensum oldPensum, Pensum newPensum) {
        PensumChangeDetector changeDetector = new PensumChangeDetector();
        PensumChangeLog changes = changeDetector.getChanges(oldPensum, newPensum);
        newPensum.addChangeLog(changes);
        pensumRepository.save(newPensum);
    }
}
