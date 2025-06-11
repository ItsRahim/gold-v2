package com.rahim.emailservice.exception;

/**
 * @created 10/06/2025
 * @author Rahim Ahmed
 */
public class EmailSendingException extends RuntimeException {
  public EmailSendingException(String message) {
    super(message);
  }

  public EmailSendingException(String message, Throwable cause) {
    super(message, cause);
  }
}
