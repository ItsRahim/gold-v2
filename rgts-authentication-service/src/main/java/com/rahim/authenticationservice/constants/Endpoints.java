package com.rahim.authenticationservice.constants;

/**
 * @created 04/05/2025
 * @author Rahim Ahmed
 */
public final class Endpoints {
  private Endpoints() {}

  public static final String AUTH_SERVICE = "/api/v2/auth";
  public static final String AUTH_SERVICE_WILDCARD = AUTH_SERVICE + "/**";
  public static final String SIGNUP = "/signup";
  public static final String VERIFY = "/verify";
  public static final String RESEND = "/resend";
  public static final String LOGIN = "/login";
  public static final String LOGOUT = "/logout";
  public static final String TOKEN_INFO = "/token-info";
  public static final String REFRESH_TOKEN = "/refresh-token";
  public static final String FORGOT_PASSWORD = "/forgot-password";
  public static final String RESET_PASSWORD = "/reset-password";
}
