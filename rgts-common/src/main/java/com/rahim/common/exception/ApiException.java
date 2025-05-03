package com.rahim.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Getter
public class ApiException extends RuntimeException {
  private final HttpStatus httpStatus;

  protected ApiException(String message, HttpStatus httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
  }
}
