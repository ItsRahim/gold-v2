package com.rahim.emailservice.exception;

/**
 * @created 10/06/2025
 * @author Rahim Ahmed
 */
public class EmailProcessingException extends RuntimeException {
  public EmailProcessingException(String message) {
    super(message);
  }

  public EmailProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
