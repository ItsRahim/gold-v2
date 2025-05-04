package com.rahim.common.exception;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
public class DuplicateEntityException extends BadRequestException {
  public DuplicateEntityException(String message) {
    super(message);
  }
}
