package com.rahim.pricingservice.exception;

import com.rahim.common.exception.BadRequestException;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
public class DuplicateGoldTypeException extends BadRequestException {
  public DuplicateGoldTypeException(String message) {
    super(message);
  }
}
