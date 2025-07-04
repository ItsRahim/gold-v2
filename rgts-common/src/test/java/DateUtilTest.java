import static org.junit.jupiter.api.Assertions.*;

import com.rahim.common.util.DateUtil;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
}
