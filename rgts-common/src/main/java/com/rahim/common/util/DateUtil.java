package com.rahim.common.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @created 04/05/2025
 * @author Rahim Ahmed
 */
public final class DateUtil {
  private DateUtil() {}

  public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
  private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

  public static String formatInstant(Instant instant) {
    return DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).withZone(UTC_ZONE_ID).format(instant);
  }

  public static String formatInstant(Instant instant, String pattern) {
    return DateTimeFormatter.ofPattern(pattern).withZone(UTC_ZONE_ID).format(instant);
  }

  public static String generateFormattedInstant() {
    return formatInstant(Instant.now());
  }

  public static Instant generateInstant() {
    return Instant.now().atZone(UTC_ZONE_ID).toInstant();
  }
}
