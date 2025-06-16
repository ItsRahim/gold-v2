package com.rahim.common.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
public class ResourceConflictException extends ApiException {
  public ResourceConflictException(String message) {
    super(message, HttpStatus.CONFLICT);
  }
}
