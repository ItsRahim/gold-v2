package com.rahim.pricingservice.converter;

import com.rahim.pricingservice.enums.WeightUnit;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * @created 21/04/2025
 * @author Rahim Ahmed
 */
@Converter(autoApply = true)
public class WeightUnitConverter implements AttributeConverter<WeightUnit, String> {

  @Override
  public String convertToDatabaseColumn(WeightUnit unit) {
    return unit == null ? null : unit.getValue();
  }

  @Override
  public WeightUnit convertToEntityAttribute(String value) {
    return value == null ? null : WeightUnit.fromValue(value);
  }
}
