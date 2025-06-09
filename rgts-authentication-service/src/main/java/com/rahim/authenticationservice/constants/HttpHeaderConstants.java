package com.rahim.authenticationservice.constants;

import org.springframework.http.HttpHeaders;

/** Constants for HTTP headers used in the application. */
public final class HttpHeaderConstants {
  private HttpHeaderConstants() {}

  public static final String AUTHORIZATION = HttpHeaders.AUTHORIZATION;
  public static final String CACHE_CONTROL = HttpHeaders.CACHE_CONTROL;
  public static final String CONTENT_TYPE = HttpHeaders.CONTENT_TYPE;

  public static final String X_TOTAL_COUNT = "X-Total-Count";
  public static final String X_RATE_LIMIT_REMAINING = "X-Rate-Limit-Remaining";
  public static final String X_RATE_LIMIT_RESET = "X-Rate-Limit-Reset";
}
