package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.ChangeType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class GroupChangeLogDto {
    private Long id;
    private String code;
    private ChangeType type;
    private Map<String, ChangedValueDto<?>> fieldChanges = new HashMap<>();
}

