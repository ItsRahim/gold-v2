package com.rahim.pricingservice.exception;

import com.rahim.common.exception.base.NotFoundException;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
public class GoldTypeNotFoundException extends NotFoundException {
  public GoldTypeNotFoundException(String message) {
    super(message);
  }
}
