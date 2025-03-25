package com.rahim.common.exception;

import com.rahim.common.exception.base.BadRequestException;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
public class DuplicateEntityException extends BadRequestException {
  public DuplicateEntityException(String message) {
    super(message);
  }
}
