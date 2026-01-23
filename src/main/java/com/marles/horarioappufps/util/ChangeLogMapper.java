package com.marles.horarioappufps.util;

import com.marles.horarioappufps.dto.response.ChangedValueDto;
import com.marles.horarioappufps.dto.response.GroupChangeLogDto;
import com.marles.horarioappufps.dto.response.PensumChangeLogDto;
import com.marles.horarioappufps.dto.response.SubjectChangeLogDto;
import com.marles.horarioappufps.model.GroupChangeLog;
import com.marles.horarioappufps.model.PensumChangeLog;
import com.marles.horarioappufps.model.SubjectChangeLog;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ChangeLogMapper {

    public PensumChangeLogDto toDto(PensumChangeLog entity) {
        if (entity == null) {
            return null;
        }

        PensumChangeLogDto dto = new PensumChangeLogDto();
        dto.setId(entity.getId());
        dto.setDate(entity.getDate());
        dto.setPensumId(entity.getPensum().getId());
        dto.setPensumName(entity.getPensum().getName());

        if (entity.getSubjectChangeLog() != null) {
            dto.setSubjectChangeLogs(
                entity.getSubjectChangeLog().stream()
                    .map(this::toSubjectChangeLogDto)
                    .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public SubjectChangeLogDto toSubjectChangeLogDto(SubjectChangeLog entity) {
        if (entity == null) {
            return null;
        }

        SubjectChangeLogDto dto = new SubjectChangeLogDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setType(entity.getType());
        dto.setName(entity.getName());
        dto.setFieldChanges(convertFieldChanges(entity.getFieldChanges()));

        if (entity.getGroupChangeLogs() != null) {
            dto.setGroupChangeLogs(
                entity.getGroupChangeLogs().stream()
                    .map(this::toGroupChangeLogDto)
                    .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public GroupChangeLogDto toGroupChangeLogDto(GroupChangeLog entity) {
        if (entity == null) {
            return null;
        }

        GroupChangeLogDto dto = new GroupChangeLogDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setType(entity.getType());
        dto.setFieldChanges(convertFieldChanges(entity.getFieldChanges()));

        return dto;
    }

    private Map<String, ChangedValueDto<?>> convertFieldChanges(Map<String, ChangedValue<?>> fieldChanges) {
        if (fieldChanges == null) {
            return new HashMap<>();
        }

        Map<String, ChangedValueDto<?>> result = new HashMap<>();
        fieldChanges.forEach((key, value) -> {
            ChangedValueDto<?> dto = new ChangedValueDto<>(
                value.oldValue,
                value.newValue
            );
            result.put(key, dto);
        });

        return result;
    }
}

