package com.rahim.authenticationservice.exception;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
public class SendEmailException extends RuntimeException {
  public SendEmailException(String message) {
    super(message);
  }

  public SendEmailException(String message, Throwable cause) {
    super(message, cause);
  }
}
