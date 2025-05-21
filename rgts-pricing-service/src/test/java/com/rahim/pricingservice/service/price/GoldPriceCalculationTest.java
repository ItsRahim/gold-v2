package com.rahim.pricingservice.service.price;

import static org.junit.jupiter.api.Assertions.*;

import com.rahim.pricingservice.BaseUnitTest;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.exception.GoldPriceCalculationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

public class GoldPriceCalculationTest extends BaseUnitTest {
  @Autowired private GoldPriceCalculationService goldPriceCalculationService;

  private static final BigDecimal TROY_OUNCE_TO_GRAMS = BigDecimal.valueOf(31.1035);
  private static final BigDecimal GRAMS_TO_KILOGRAM = BigDecimal.valueOf(1000);

  @Nested
  @DisplayName("Calculate Price Per Gram For Purity Tests")
  class CalculatePricePerGramForPurityTests {

    @ParameterizedTest
    @MethodSource("providePurityAndExpectedPrice")
    void shouldCalculatePricePerGram(int numerator, int denominator, BigDecimal pricePerTroyOunce) {
      GoldPurity purity = new GoldPurity();
      purity.setNumerator(numerator);
      purity.setDenominator(denominator);

      BigDecimal result =
          goldPriceCalculationService.calculatePricePerGramForPurity(pricePerTroyOunce, purity);

      BigDecimal expected =
          pricePerTroyOunce
              .divide(TROY_OUNCE_TO_GRAMS, 10, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf((double) numerator / denominator))
              .setScale(2, RoundingMode.HALF_UP);

      assertEquals(expected, result);
    }

    private static Stream<Arguments> providePurityAndExpectedPrice() {
      BigDecimal pricePerTroyOunce = BigDecimal.valueOf(2000);

      return Stream.of(
          Arguments.of(24, 24, pricePerTroyOunce),
          Arguments.of(22, 24, pricePerTroyOunce),
          Arguments.of(18, 24, pricePerTroyOunce));
    }

    @Test
    void shouldReturnZeroWhenPriceIsNull() {
      GoldPurity purity = new GoldPurity();
      purity.setNumerator(24);
      purity.setDenominator(24);

      BigDecimal result = goldPriceCalculationService.calculatePricePerGramForPurity(null, purity);

      assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void shouldReturnZeroWhenPurityIsNull() {
      BigDecimal pricePerTroyOunce = BigDecimal.valueOf(2000);

      BigDecimal result =
          goldPriceCalculationService.calculatePricePerGramForPurity(pricePerTroyOunce, null);

      assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void shouldReturnZeroWhenBothAreNull() {
      BigDecimal result = goldPriceCalculationService.calculatePricePerGramForPurity(null, null);

      assertEquals(BigDecimal.ZERO, result);
    }
  }

  @Nested
  @DisplayName("Calculate Price Tests")
  class CalculatePriceTests {

    @Test
    void shouldCalculateCorrectPriceInGrams() {
      BigDecimal pricePerGram = BigDecimal.valueOf(64.30);
      BigDecimal weight = BigDecimal.valueOf(10);
      WeightUnit unit = WeightUnit.GRAM;

      BigDecimal result = goldPriceCalculationService.calculatePrice(pricePerGram, weight, unit);

      BigDecimal expected = pricePerGram.multiply(weight).setScale(2, RoundingMode.HALF_UP);
      assertEquals(expected, result);
      assertEquals(new BigDecimal("643.00"), result);
    }

    @Test
    void shouldCalculateCorrectPriceInKilograms() {
      BigDecimal pricePerGram = BigDecimal.valueOf(64.30);
      BigDecimal weight = BigDecimal.valueOf(0.5);
      WeightUnit unit = WeightUnit.KILOGRAM;

      BigDecimal result = goldPriceCalculationService.calculatePrice(pricePerGram, weight, unit);

      BigDecimal weightInGrams = weight.multiply(GRAMS_TO_KILOGRAM);
      BigDecimal expected = pricePerGram.multiply(weightInGrams).setScale(2, RoundingMode.HALF_UP);
      assertEquals(expected, result);
      assertEquals(new BigDecimal("32150.00"), result);
    }

    @Test
    void shouldThrowExceptionWhenPricePerGramIsNull() {
      BigDecimal weight = BigDecimal.valueOf(10);
      WeightUnit unit = WeightUnit.GRAM;

      GoldPriceCalculationException exception =
          assertThrows(
              GoldPriceCalculationException.class,
              () -> goldPriceCalculationService.calculatePrice(null, weight, unit));
      assertEquals("Price per gram cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenWeightIsNull() {
      BigDecimal pricePerGram = BigDecimal.valueOf(64.30);
      WeightUnit unit = WeightUnit.GRAM;

      GoldPriceCalculationException exception =
          assertThrows(
              GoldPriceCalculationException.class,
              () -> goldPriceCalculationService.calculatePrice(pricePerGram, null, unit));
      assertEquals("Weight must be greater than zero", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-10.5"})
    void shouldThrowExceptionWhenWeightIsZeroOrNegative(String weightValue) {
      BigDecimal pricePerGram = BigDecimal.valueOf(64.30);
      BigDecimal weight = new BigDecimal(weightValue);
      WeightUnit unit = WeightUnit.GRAM;

      GoldPriceCalculationException exception =
          assertThrows(
              GoldPriceCalculationException.class,
              () -> goldPriceCalculationService.calculatePrice(pricePerGram, weight, unit));
      assertEquals("Weight must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenWeightUnitIsNull() {
      BigDecimal pricePerGram = BigDecimal.valueOf(64.30);
      BigDecimal weight = BigDecimal.valueOf(10);

      GoldPriceCalculationException exception =
          assertThrows(
              GoldPriceCalculationException.class,
              () -> goldPriceCalculationService.calculatePrice(pricePerGram, weight, null));
      assertEquals("Weight unit must be provided", exception.getMessage());
    }
  }

  @Nested
  @DisplayName("Convert Weight To Grams Tests")
  class ConvertWeightToGramsTests {

    @Test
    void shouldMaintainWeightWhenUnitIsGrams() {
      BigDecimal pricePerGram = BigDecimal.valueOf(64.30);
      BigDecimal weight = BigDecimal.valueOf(15.5);
      WeightUnit unit = WeightUnit.GRAM;

      BigDecimal result = goldPriceCalculationService.calculatePrice(pricePerGram, weight, unit);

      BigDecimal expected = pricePerGram.multiply(weight).setScale(2, RoundingMode.HALF_UP);
      assertEquals(expected, result);
    }

    @Test
    void shouldConvertWeightFromKilogramsToGrams() {
      BigDecimal pricePerGram = BigDecimal.valueOf(64.30);
      BigDecimal weightInKg = BigDecimal.valueOf(0.0015);
      WeightUnit unit = WeightUnit.KILOGRAM;

      BigDecimal expectedWeightInGrams = weightInKg.multiply(GRAMS_TO_KILOGRAM);

      BigDecimal result =
          goldPriceCalculationService.calculatePrice(pricePerGram, weightInKg, unit);

      BigDecimal expected =
          pricePerGram.multiply(expectedWeightInGrams).setScale(2, RoundingMode.HALF_UP);
      assertEquals(expected, result);
    }

    @Test
    void shouldConvertWeightFromOuncesToGrams() {
      BigDecimal pricePerGram = BigDecimal.valueOf(64.30);
      BigDecimal weightInOz = BigDecimal.valueOf(0.5);
      WeightUnit unit = WeightUnit.OUNCE;

      BigDecimal expectedWeightInGrams = weightInOz.multiply(TROY_OUNCE_TO_GRAMS);

      BigDecimal result =
          goldPriceCalculationService.calculatePrice(pricePerGram, weightInOz, unit);

      BigDecimal expected =
          pricePerGram.multiply(expectedWeightInGrams).setScale(2, RoundingMode.HALF_UP);
      assertEquals(expected, result);
    }
  }

  @Nested
  @DisplayName("Integration Tests")
  class IntegrationTests {

    @Test
    void shouldHandleVerySmallWeightsCorrectly() {
      BigDecimal pricePerGram = BigDecimal.valueOf(64.30);
      BigDecimal weight = BigDecimal.valueOf(0.001);
      WeightUnit unit = WeightUnit.GRAM;

      BigDecimal result = goldPriceCalculationService.calculatePrice(pricePerGram, weight, unit);

      BigDecimal expected = pricePerGram.multiply(weight).setScale(2, RoundingMode.HALF_UP);
      assertEquals(expected, result);
      assertEquals(new BigDecimal("0.06"), result);
    }

    @Test
    void shouldHandleVeryLargeWeightsCorrectly() {
      BigDecimal pricePerGram = BigDecimal.valueOf(64.30);
      BigDecimal weight = BigDecimal.valueOf(100);
      WeightUnit unit = WeightUnit.KILOGRAM;

      BigDecimal result = goldPriceCalculationService.calculatePrice(pricePerGram, weight, unit);

      BigDecimal weightInGrams = weight.multiply(GRAMS_TO_KILOGRAM);
      BigDecimal expected = pricePerGram.multiply(weightInGrams).setScale(2, RoundingMode.HALF_UP);
      assertEquals(expected, result);
      assertEquals(new BigDecimal("6430000.00"), result);
    }

    @ParameterizedTest
    @MethodSource("provideGoldPurityValues")
    void shouldCalculatePricesForVariousPurities(
        int numerator, int denominator, BigDecimal expectedPurityFactor) {
      BigDecimal spotPricePerTroyOunce = BigDecimal.valueOf(2000);
      GoldPurity purity = new GoldPurity();
      purity.setNumerator(numerator);
      purity.setDenominator(denominator);

      BigDecimal result =
          goldPriceCalculationService.calculatePricePerGramForPurity(spotPricePerTroyOunce, purity);

      BigDecimal pricePerGram =
          spotPricePerTroyOunce.divide(TROY_OUNCE_TO_GRAMS, 10, RoundingMode.HALF_UP);
      BigDecimal expected =
          pricePerGram.multiply(expectedPurityFactor).setScale(2, RoundingMode.HALF_UP);
      assertEquals(expected, result);
    }

    private static Stream<Arguments> provideGoldPurityValues() {
      return Stream.of(
          Arguments.of(24, 24, BigDecimal.valueOf(1.0)),
          Arguments.of(22, 24, BigDecimal.valueOf(22.0 / 24)),
          Arguments.of(18, 24, BigDecimal.valueOf(18.0 / 24)),
          Arguments.of(14, 24, BigDecimal.valueOf(14.0 / 24)),
          Arguments.of(10, 24, BigDecimal.valueOf(10.0 / 24)),
          Arguments.of(9, 24, BigDecimal.valueOf(9.0 / 24)));
    }
  }
}
