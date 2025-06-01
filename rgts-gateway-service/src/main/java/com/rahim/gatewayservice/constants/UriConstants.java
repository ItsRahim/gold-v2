package com.rahim.gatewayservice.constants;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
public final class UriConstants {
  private UriConstants() {}

  public static final String PRICING_SERVICE = "pricing-service";
  public static final String PRICING_SERVICE_URI = "lb://pricing-service";
  public static final String[] PRICING_PATHS = {"/type/**", "/price/**"};
  public static final String PRICING_API_BASE = "/api/v2/pricing-service";
  public static final String PRICING_REWRITE_REGEX = "/(?<segment>type|price)(?<remaining>/.*)?";
  public static final String PRICING_REWRITE_REPLACEMENT =
      PRICING_API_BASE + "/${segment}${remaining}";
}
