package com.rahim.authenticationservice.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @created 16/06/2025
 * @author Rahim Ahmed
 */
public class RequestUtils {
  private RequestUtils() {}

  private static final String DEFAULT_TIMEZONE = "UTC";

  public static String getClientTimezone(HttpServletRequest request) {
    String timezone = request.getHeader("Time-Zone");
    return timezone != null ? timezone : DEFAULT_TIMEZONE;
  }

  public static String getClientLocale(HttpServletRequest request) {
    return request.getLocale().getLanguage();
  }
}
