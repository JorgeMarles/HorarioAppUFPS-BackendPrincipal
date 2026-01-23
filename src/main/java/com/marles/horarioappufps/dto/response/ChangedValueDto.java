package com.marles.horarioappufps.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangedValueDto<T> {
    private T oldValue;
    private T newValue;
}

