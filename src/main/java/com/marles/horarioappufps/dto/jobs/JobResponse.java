package com.marles.horarioappufps.dto.jobs;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobResponse<T extends BaseResponseData> {
    private Long jobId;
    private T data;
    private String response;
    private boolean success;
}
