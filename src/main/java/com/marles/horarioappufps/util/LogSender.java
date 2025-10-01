package com.marles.horarioappufps.util;

import com.marles.horarioappufps.dto.jobs.GenericRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
//@Primary
public class LogSender implements DataSender {

    @Override
    public <T extends GenericRequest> void send(T data) {
        log.info("Sent: {}", data.toString());
    }
}
