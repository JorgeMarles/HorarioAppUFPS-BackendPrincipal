package com.marles.horarioappufps.dto.jobs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.marles.horarioappufps.dto.request.PensumAsyncCreationDto;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PensumAsyncCreationDto.class, name = "pensum"),
        @JsonSubTypes.Type(value = SubjectResponse.class, name = "subject")
})
public abstract class BaseResponseData {
}
