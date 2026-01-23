package com.marles.horarioappufps.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter
public class FieldChangesConverter implements AttributeConverter<Map<String, ChangedValue<?>>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, ChangedValue<?>> attribute) {
        try {
            return attribute == null ? null : objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting to JSON", e);
        }
    }

    @Override
    public Map<String, ChangedValue<?>> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : objectMapper.readValue(dbData, new TypeReference<Map<String, ChangedValue<?>>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error parsing JSON", e);
        }
    }
}