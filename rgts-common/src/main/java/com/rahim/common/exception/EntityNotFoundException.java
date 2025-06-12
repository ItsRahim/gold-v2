package com.rahim.common.exception;

import org.springframework.http.HttpStatus;

/**
 * @created 03/05/2025
 * @author Rahim Ahmed
 */
public class EntityNotFoundException extends ApiException {
  public EntityNotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }
}
