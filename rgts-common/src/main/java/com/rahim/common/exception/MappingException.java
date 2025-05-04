package com.rahim.common.exception;

/**
 * @created 03/05/2025
 * @author Rahim Ahmed
 */
public class MappingException extends RuntimeException {
  public MappingException(String message, Throwable cause) {
    super(message, cause);
  }

  public MappingException(String message) {
    super(message);
  }
}
