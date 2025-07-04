import static org.junit.jupiter.api.Assertions.*;

import com.rahim.common.util.DateUtil;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

/**
 * @created 04/07/2025
 * @author Rahim Ahmed
 */
class DateUtilTest {

  @Test
  void testFormatInstantDefaultPattern() {
    Instant instant = Instant.parse("2025-06-01T12:34:56Z");
    String formatted = DateUtil.formatInstant(instant);
    assertEquals("2025-06-01 12:34:56", formatted);
  }

  @Test
  void testFormatInstantCustomPattern() {
    Instant instant = Instant.parse("2025-06-01T12:34:56Z");
    String formatted = DateUtil.formatInstant(instant, "yyyy/MM/dd HH:mm");
    assertEquals("2025/06/01 12:34", formatted);
  }

  @Test
  void testGenerateFormattedInstant() {
    String formatted = DateUtil.generateFormattedInstant();
    assertNotNull(formatted);
    assertEquals(19, formatted.length());
  }

  @Test
  void testGenerateInstant() {
    Instant now = Instant.now();
    Instant generated = DateUtil.generateInstant();
    assertFalse(generated.isBefore(now));
  }

  @Test
  void testNowUtc() {
    OffsetDateTime now = DateUtil.nowUtc();
    assertEquals(ZoneOffset.UTC, now.getOffset());
  }

  @Test
  void testNowUtcFormatted() {
    String formatted = DateUtil.nowUtcFormatted();
    assertNotNull(formatted);
    assertEquals(19, formatted.length());
  }

  @Test
  void testAddMinutesToNowUtc() {
    OffsetDateTime now = DateUtil.nowUtc();
    OffsetDateTime later = DateUtil.addMinutesToNowUtc(10);
    assertTrue(later.isAfter(now) || later.isEqual(now.plusMinutes(10)));
  }

  @Test
  void testAddMinutesToNowUtcWithBase() {
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    OffsetDateTime later = DateUtil.addMinutesToNowUtc(now, 15);
    assertEquals(now.plusMinutes(15), later);
  }

  @Test
  void testAddToNowUtc() {
    OffsetDateTime now = DateUtil.nowUtc();
    OffsetDateTime later = DateUtil.addToNowUtc(2, ChronoUnit.HOURS);
    assertTrue(later.isAfter(now) || later.isEqual(now.plusHours(2)));
  }

  @Test
  void testFormatOffsetDateTimeDefaultPattern() {
    OffsetDateTime dateTime = OffsetDateTime.of(2025, 6, 1, 12, 34, 56, 0, ZoneOffset.UTC);
    String formatted = DateUtil.formatOffsetDateTime(dateTime);
    assertEquals("2025-06-01 12:34:56", formatted);
  }

  @Test
  void testFormatOffsetDateTimeCustomPattern() {
    OffsetDateTime dateTime = OffsetDateTime.of(2025, 6, 1, 12, 34, 56, 0, ZoneOffset.UTC);
    String formatted = DateUtil.formatOffsetDateTime(dateTime, "dd/MM/yyyy HH:mm");
    assertEquals("01/06/2025 12:34", formatted);
  }

  @Test
  void testFormatInstantInDifferentTimezones() {
    Instant instant = Instant.parse("2025-06-01T12:34:56Z");
    ZoneId estZone = ZoneId.of("America/New_York");
    ZoneId jstZone = ZoneId.of("Asia/Tokyo");
    ZoneId istZone = ZoneId.of("Asia/Kolkata");

    String formattedEst = instant.atZone(estZone).format(DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_PATTERN));
    String formattedJst = instant.atZone(jstZone).format(DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_PATTERN));
    String formattedIst = instant.atZone(istZone).format(DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_PATTERN));

    assertEquals("2025-06-01 08:34:56", formattedEst);
    assertEquals("2025-06-01 21:34:56", formattedJst);
    assertEquals("2025-06-01 18:04:56", formattedIst);
  }

  @Test
  void testNowUtcWithDifferentTimezones() {
    OffsetDateTime utcNow = DateUtil.nowUtc();
    OffsetDateTime estTime = utcNow.atZoneSameInstant(ZoneId.of("America/New_York")).toOffsetDateTime();
    OffsetDateTime jstTime = utcNow.atZoneSameInstant(ZoneId.of("Asia/Tokyo")).toOffsetDateTime();
    OffsetDateTime istTime = utcNow.atZoneSameInstant(ZoneId.of("Asia/Kolkata")).toOffsetDateTime();

    assertEquals(ZoneOffset.UTC, utcNow.getOffset());
    assertEquals(ZoneOffset.ofHours(-4), estTime.getOffset());
    assertEquals(ZoneOffset.ofHours(9), jstTime.getOffset());
    assertEquals(ZoneOffset.ofHoursMinutes(5, 30), istTime.getOffset());

    assertEquals(utcNow.toInstant(), estTime.toInstant());
    assertEquals(utcNow.toInstant(), jstTime.toInstant());
    assertEquals(utcNow.toInstant(), istTime.toInstant());
  }

  @Test
  void testAddMinutesToNowUtcWithDifferentTimezones() {
    OffsetDateTime utcBase = OffsetDateTime.of(2025, 6, 1, 12, 0, 0, 0, ZoneOffset.UTC);
    OffsetDateTime utcPlus60 = DateUtil.addMinutesToNowUtc(utcBase, 60);

    OffsetDateTime estPlus60 = utcPlus60.atZoneSameInstant(ZoneId.of("America/New_York")).toOffsetDateTime();
    OffsetDateTime jstPlus60 = utcPlus60.atZoneSameInstant(ZoneId.of("Asia/Tokyo")).toOffsetDateTime();

    assertEquals("2025-06-01 13:00:00", DateUtil.formatOffsetDateTime(utcPlus60));
    assertEquals("2025-06-01 09:00:00", DateUtil.formatOffsetDateTime(estPlus60));
    assertEquals("2025-06-01 22:00:00", DateUtil.formatOffsetDateTime(jstPlus60));
  }
}
