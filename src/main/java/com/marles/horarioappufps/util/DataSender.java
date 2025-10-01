package com.marles.horarioappufps.util;

import com.marles.horarioappufps.dto.jobs.GenericRequest;

public interface DataSender {
    <T extends GenericRequest> void send(T data);
}
