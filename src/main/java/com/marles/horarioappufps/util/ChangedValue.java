package com.marles.horarioappufps.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChangedValue<T> {
    T oldValue;
    T newValue;
}
