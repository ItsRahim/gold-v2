package com.rahim.common.exception;

/**
 * @created 03/05/2025
 * @author Rahim Ahmed
 */
public class EntityNotFoundException extends RuntimeException {
  public EntityNotFoundException(String message) {
    super(message);
  }
}
