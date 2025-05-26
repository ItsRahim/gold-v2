package com.rahim.common.handler;

import com.rahim.common.exception.ApiException;
import com.rahim.common.exception.EntityNotFoundException;
import com.rahim.common.response.ErrorResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResponse> apiExceptionHandler(ApiException ex) {
    ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getHttpStatus());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> entityNotFoundHandler(EntityNotFoundException ex) {
    ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {
    List<String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                fieldError ->
                    String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()))
            .toList();

    String message = "Validation failed: " + String.join("; ", errors);
    ErrorResponse errorResponse = new ErrorResponse(message, HttpStatus.BAD_REQUEST);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex) {
    ErrorResponse errorResponse =
        new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }
}
