package com.rahim.authenticationservice.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * @created 16/06/2025
 * @author Rahim Ahmed
 */
@Component
public class RequestUtil {
  private RequestUtil() {}

  private static final String DEFAULT_TIMEZONE = "UTC";

  public String getClientTimezone(HttpServletRequest request) {
    String timezone = request.getHeader("Time-Zone");
    return timezone != null ? timezone : DEFAULT_TIMEZONE;
  }

  public String getClientLocale(HttpServletRequest request) {
    return request.getLocale().getLanguage();
  }
}
