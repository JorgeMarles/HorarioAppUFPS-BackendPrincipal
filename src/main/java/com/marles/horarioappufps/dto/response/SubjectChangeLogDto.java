package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.ChangeType;
import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class SubjectChangeLogDto {
    private Long id;
    private String code;
    private ChangeType type;
    private String name;
    private List<GroupChangeLogDto> groupChangeLogs = new LinkedList<>();
    private Map<String, ChangedValueDto<?>> fieldChanges = new HashMap<>();
}

