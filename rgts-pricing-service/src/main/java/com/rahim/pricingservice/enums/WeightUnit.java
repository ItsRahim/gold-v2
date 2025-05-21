package com.rahim.pricingservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

/**
 * @created 21/04/2025
 * @author Rahim Ahmed
 */
@AllArgsConstructor
public enum WeightUnit {
  GRAM("g"),
  KILOGRAM("kg"),
  OUNCE("oz");

  private final String value;

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static WeightUnit fromValue(String value) {
    for (WeightUnit weightUnit : WeightUnit.values()) {
      if (weightUnit.getValue().equalsIgnoreCase(value)) {
        return weightUnit;
      }
    }
    throw new IllegalArgumentException("Invalid weight unit: " + value);
  }
}
