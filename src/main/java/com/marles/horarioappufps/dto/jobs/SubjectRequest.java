package com.marles.horarioappufps.dto.jobs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class SubjectRequest extends GenericRequest{
    private String code;

    private boolean isPrincipal;

    public SubjectRequest(Long jobId, String cookie, String code, boolean isPrincipal) {
        super(jobId, cookie, RequestType.SUBJECT);
        this.code = code;
        this.isPrincipal = isPrincipal;
    }
}
