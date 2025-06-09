package com.rahim.authenticationservice.exception;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String message) {
    super(message);
  }

  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
