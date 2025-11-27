package com.marles.horarioappufps.exception;

import com.marles.horarioappufps.dto.response.schedule.ScheduleMessage;
import lombok.Getter;
import lombok.experimental.StandardException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class RequisiteConflictException extends RuntimeException {
  private List<String> messages;

  public RequisiteConflictException(String error) {
    super(error);
    this.messages = new LinkedList<>();
  }
}

