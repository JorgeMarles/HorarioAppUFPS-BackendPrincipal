package com.marles.horarioappufps.util;

import com.marles.horarioappufps.dto.jobs.GenericRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PubSubSender implements DataSender {


    @Override
    public <T extends GenericRequest> void send(T data) {

    }
}
