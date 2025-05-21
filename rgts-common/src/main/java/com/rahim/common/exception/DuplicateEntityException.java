package com.rahim.common.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
public class DuplicateEntityException extends ApiException {
  public DuplicateEntityException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }
}
