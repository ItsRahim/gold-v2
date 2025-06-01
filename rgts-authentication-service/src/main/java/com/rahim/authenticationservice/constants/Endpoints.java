package com.rahim.authenticationservice.constants;

/**
 * @created 04/05/2025
 * @author Rahim Ahmed
 */
public final class Endpoints {
  private Endpoints() {}

  public static final String AUTH_SERVICE = "/api/v2/auth";
  public static final String SIGNUP = AUTH_SERVICE + "/signup";
  public static final String VERIFY = AUTH_SERVICE + "/verify";
  public static final String RESEND = AUTH_SERVICE + "/resend";
  public static final String LOGIN = AUTH_SERVICE + "/login";
  public static final String LOGOUT = AUTH_SERVICE + "/logout";
  public static final String TOKEN_INFO = AUTH_SERVICE + "/token-info";
  public static final String REFRESH_TOKEN = AUTH_SERVICE + "/refresh-token";
  public static final String FORGOT_PASSWORD = AUTH_SERVICE + "/forgot-password";
  public static final String RESET_PASSWORD = AUTH_SERVICE + "/reset-password";
}
