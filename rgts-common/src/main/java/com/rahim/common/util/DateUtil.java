package com.rahim.common.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * @created 04/05/2025
 * @author Rahim Ahmed
 */
public final class DateUtil {
  private DateUtil() {}

  public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
  private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");
  private static final DateTimeFormatter DEFAULT_FORMATTER =
      DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).withZone(UTC_ZONE_ID);

  public static String formatInstant(Instant instant) {
    return DEFAULT_FORMATTER.format(instant);
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

  public static OffsetDateTime nowUtc() {
    return OffsetDateTime.now(ZoneOffset.UTC);
  }

  public static String nowUtcFormatted() {
    return nowUtc().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
  }

  public static OffsetDateTime addMinutesToNowUtc(int minutes) {
    return nowUtc().plusMinutes(minutes);
  }

  public static OffsetDateTime addMinutesToNowUtc(OffsetDateTime now, int minutes) {
    return now.plusMinutes(minutes);
  }

  public static OffsetDateTime addToNowUtc(long amount, ChronoUnit unit) {
    return nowUtc().plus(amount, unit);
  }

  public static String formatOffsetDateTime(OffsetDateTime dateTime) {
    return dateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
  }

  public static String formatOffsetDateTime(OffsetDateTime dateTime, String pattern) {
    return dateTime.format(DateTimeFormatter.ofPattern(pattern));
  }
}
