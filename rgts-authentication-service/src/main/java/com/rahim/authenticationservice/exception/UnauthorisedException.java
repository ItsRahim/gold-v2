package com.rahim.authenticationservice.exception;

import com.rahim.common.exception.ApiException;
import org.springframework.http.HttpStatus;

/**
 * @created 23/07/2025
 * @author Rahim Ahmed
 */
public class UnauthorisedException extends ApiException {
  public UnauthorisedException(String message) {
    super(message, HttpStatus.UNAUTHORIZED);
  }
}
