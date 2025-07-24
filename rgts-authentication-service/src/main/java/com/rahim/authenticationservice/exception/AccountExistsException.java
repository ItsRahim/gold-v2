package com.rahim.authenticationservice.exception;

import com.rahim.common.exception.ApiException;
import org.springframework.http.HttpStatus;

/**
 * @created 24/07/2025
 * @author Rahim Ahmed
 */
public class AccountExistsException extends ApiException {
  public AccountExistsException(String message) {
    super(message, HttpStatus.CONFLICT);
  }
}
