package com.rahim.pricingservice.exception;

/**
 * @created 04/05/2025
 * @author Rahim Ahmed
 */
public class GoldPriceCalculationException extends RuntimeException {
  public GoldPriceCalculationException(String message) {
    super(message);
  }

  public GoldPriceCalculationException(String message, Throwable cause) {
    super(message, cause);
  }
}
