package com.marles.horarioappufps.dto.jobs;

import com.marles.horarioappufps.dto.request.SubjectGroupCreationDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SubjectResponse extends BaseResponseData {
    private String code;
    private Map<String, SubjectGroupCreationDto> groups = new HashMap<>();
}
