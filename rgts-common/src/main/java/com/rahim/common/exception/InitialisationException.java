package com.rahim.common.exception;

/**
 * @created 03/05/2025
 * @author Rahim Ahmed
 */
public class InitialisationException extends RuntimeException {
  public InitialisationException(String message, Throwable cause) {
    super(message, cause);
  }

  public InitialisationException(String message) {
    super(message);
  }
}
