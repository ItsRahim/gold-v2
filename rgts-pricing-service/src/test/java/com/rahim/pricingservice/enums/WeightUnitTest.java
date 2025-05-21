package com.rahim.pricingservice.enums;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author Rahim Ahmed
 * @created 21/05/2025
 */
public class WeightUnitTest {

  @Test
  public void shouldReturnCorrectValueForWeightUnit() {
    assertThat(WeightUnit.GRAM.getValue()).isEqualTo("g");
    assertThat(WeightUnit.KILOGRAM.getValue()).isEqualTo("kg");
    assertThat(WeightUnit.OUNCE.getValue()).isEqualTo("oz");
  }

  @Test
  public void shouldReturnCorrectWeightUnitFromValue() {
    assertThat(WeightUnit.fromValue("g")).isEqualTo(WeightUnit.GRAM);
    assertThat(WeightUnit.fromValue("kg")).isEqualTo(WeightUnit.KILOGRAM);
    assertThat(WeightUnit.fromValue("oz")).isEqualTo(WeightUnit.OUNCE);
  }

  @Test
  public void shouldBeCaseInsensitive() {
    assertThat(WeightUnit.fromValue("G")).isEqualTo(WeightUnit.GRAM);
    assertThat(WeightUnit.fromValue("Kg")).isEqualTo(WeightUnit.KILOGRAM);
    assertThat(WeightUnit.fromValue("OZ")).isEqualTo(WeightUnit.OUNCE);
  }

  @ParameterizedTest
  @ValueSource(strings = {"ml", "lb", "ton", "", " ", "grams", "kgs", "ounces"})
  public void shouldThrowExceptionForInvalidWeightUnit(String invalidUnit) {
    assertThatThrownBy(() -> WeightUnit.fromValue(invalidUnit))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid weight unit: " + invalidUnit);
  }

  @Test
  public void shouldThrowExceptionForNullWeightUnit() {
    assertThatThrownBy(() -> WeightUnit.fromValue(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid weight unit: null");
  }
}
