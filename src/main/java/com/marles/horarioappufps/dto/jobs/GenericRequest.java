package com.marles.horarioappufps.dto.jobs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class GenericRequest {
    Long jobId;
    String cookie;
    RequestType type;
}
