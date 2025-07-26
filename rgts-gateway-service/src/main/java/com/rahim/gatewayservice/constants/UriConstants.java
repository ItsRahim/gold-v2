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

  public static final String AUTHENTICATION_SERVICE = "authentication-service";
  public static final String AUTHENTICATION_SERVICE_URI = "lb://authentication-service";
  public static final String[] AUTHENTICATION_PATHS = {"/auth/**", "/user/**"};
  public static final String AUTHENTICATION_API_BASE = "/api/v2/authentication-service";
  public static final String AUTHENTICATION_REWRITE_REGEX =
      "/(?<segment>auth|user)(?<remaining>/.*)?";
  public static final String AUTHENTICATION_REWRITE_REPLACEMENT =
      AUTHENTICATION_API_BASE + "${remaining}";
}
