package com.marles.horarioappufps.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marles.horarioappufps.dto.jobs.GenericRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Primary
public class JsonLogSender implements DataSender {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T extends GenericRequest> void send(T data) {
        try {
            String jsonString = objectMapper.writeValueAsString(data);
            log.info("Enviando mensaje: {}", jsonString);
        } catch (JsonProcessingException e) {
            log.error("Error al convertir objeto a JSON", e);
        }
    }
}
