package com.rahim.common.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
public class ServiceException extends ApiException {
  public ServiceException(String message) {
    super(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
