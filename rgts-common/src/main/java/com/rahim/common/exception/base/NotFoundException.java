package com.rahim.common.exception.base;

import org.springframework.http.HttpStatus;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
public class NotFoundException extends ApiException {
  public NotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }
}
