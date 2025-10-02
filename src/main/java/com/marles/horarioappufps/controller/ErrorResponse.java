package com.marles.horarioappufps.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResponse {
    private long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorResponse(HttpStatus status, String error, Exception ex, HttpServletRequest request) {
        this.timestamp = System.currentTimeMillis();
        this.status = status.value();
        this.error = error;
        this.message = ex.getMessage();
        this.path = request.getRequestURI();
    }

    public ErrorResponse(String message, HttpServletRequest request) {
        this.timestamp = System.currentTimeMillis();
        this.status = HttpStatus.BAD_REQUEST.value();
        this.error = "Bad Request";
        this.message = message;
        this.path = request.getRequestURI();
    }
}
