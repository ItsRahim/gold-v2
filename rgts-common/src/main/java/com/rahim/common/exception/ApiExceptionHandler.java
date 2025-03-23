package com.rahim.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> apiExceptionHandler(ApiException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }
}
