package com.rahim.pricingservice.constant;

/**
 * @created 04/05/2025
 * @author Rahim Ahmed
 */
public final class Endpoints {
  private Endpoints() {}

  private static final String PRICING_SERVICE = "/api/v2/pricing-service";
  public static final String GOLD_TYPE_ENDPOINT = PRICING_SERVICE + "/type";
  public static final String GOLD_PRICE_ENDPOINT = PRICING_SERVICE + "/price";
}
