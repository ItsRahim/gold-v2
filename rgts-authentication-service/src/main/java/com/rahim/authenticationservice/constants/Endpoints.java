package com.rahim.authenticationservice.constants;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
public final class Endpoints {
  private Endpoints() {}

  public static final String API_VERSION = "/api/v2";
  public static final String AUTH_SERVICE = API_VERSION + "/authentication-service";

  // Authentication endpoints
  public static final String LOGIN = "/login";
  public static final String LOGIN_ENDPOINT = AUTH_SERVICE + LOGIN;
  public static final String VERIFY_EMAIL = "/verify-email";
  public static final String VERIFY_ENDPOINT = AUTH_SERVICE + VERIFY_EMAIL;

  public static final String REGISTER = "/register";
  public static final String REGISTER_ENDPOINT = AUTH_SERVICE + REGISTER;

  // Token management endpoints
  public static final String REFRESH_TOKEN = "/refresh-token";
  public static final String REFRESH_TOKEN_ENDPOINT = AUTH_SERVICE + REFRESH_TOKEN;
  public static final String VALIDATE_TOKEN = "/validate-token";
  public static final String VALIDATE_TOKEN_ENDPOINT = AUTH_SERVICE + VALIDATE_TOKEN;

  // Documentation endpoints
  public static final String API_DOCS = "/v3/api-docs/**";
  public static final String SWAGGER_UI = "/swagger-ui/**";
  public static final String SWAGGER_UI_HTML = "/swagger-ui.html";

  // Monitoring endpoints
  public static final String ACTUATOR_HEALTH = "/actuator/health";
  public static final String ACTUATOR_INFO = "/actuator/info";
}
