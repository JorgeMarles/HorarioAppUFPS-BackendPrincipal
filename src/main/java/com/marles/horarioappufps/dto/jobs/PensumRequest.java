package com.marles.horarioappufps.dto.jobs;


public class PensumRequest extends GenericRequest {
    public PensumRequest(Long jobId, String cookie) {
        super(jobId, cookie, RequestType.PENSUM);
    }
}
