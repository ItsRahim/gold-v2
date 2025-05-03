package com.rahim.pricingservice.exception;

import com.rahim.common.exception.NotFoundException;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
public class GoldTypeNotFoundException extends NotFoundException {
  public GoldTypeNotFoundException(String message) {
    super(message);
  }
}
