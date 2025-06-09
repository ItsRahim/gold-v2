package com.rahim.authenticationservice.constants;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
public final class Endpoints {
  private Endpoints() {}

  public static final String API_VERSION = "/api/v2";
  public static final String AUTH_SERVICE = API_VERSION + "/auth";

  // Authentication endpoints
  public static final String LOGIN = "/login";
  public static final String LOGIN_ENDPOINT = AUTH_SERVICE + LOGIN;

  public static final String REGISTER = "/register";
  public static final String REGISTER_ENDPOINT = AUTH_SERVICE + REGISTER;

  // Token management endpoints
  public static final String TOKENS = AUTH_SERVICE + "/tokens";
  public static final String REFRESH_TOKEN = TOKENS + "/refresh";
  public static final String VALIDATE_TOKEN = TOKENS + "/validate";

  // Documentation endpoints
  public static final String API_DOCS = "/v3/api-docs/**";
  public static final String SWAGGER_UI = "/swagger-ui/**";
  public static final String SWAGGER_UI_HTML = "/swagger-ui.html";

  // Monitoring endpoints
  public static final String ACTUATOR_HEALTH = "/actuator/health";
  public static final String ACTUATOR_INFO = "/actuator/info";
}
