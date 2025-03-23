package com.rahim.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Getter
public class ErrorResponse {
    private final String message;
    private final int status;
    private final LocalDateTime timestamp;

    public ErrorResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status.value();
        this.timestamp = LocalDateTime.now(ZoneOffset.UTC);
    }
}
