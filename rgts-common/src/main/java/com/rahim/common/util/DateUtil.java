package com.rahim.common.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @created 04/05/2025
 * @author Rahim Ahmed
 */
public class DateUtil {
  private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

  public static String formatInstant(Instant instant) {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(UTC_ZONE_ID).format(instant);
  }

  public static Instant generateInstant() {
    return Instant.now().atZone(UTC_ZONE_ID).toInstant();
  }
}
