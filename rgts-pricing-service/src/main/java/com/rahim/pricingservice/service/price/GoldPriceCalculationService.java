package com.rahim.pricingservice.service.price;

import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.exception.GoldPriceCalculationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @created 04/05/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
public class GoldPriceCalculationService {
  public static final BigDecimal TROY_OUNCE_TO_GRAMS = BigDecimal.valueOf(31.1035);
  public static final BigDecimal GRAMS_TO_KILOGRAM = BigDecimal.valueOf(1000);

  public BigDecimal calculatePricePerGramForPurity(
      BigDecimal pricePerTroyOunce, GoldPurity goldPurity) {

    if (pricePerTroyOunce == null || goldPurity == null) {
      log.warn(
          "Price or gold purity is null. pricePerTroyOunce={}, goldPurity={}",
          pricePerTroyOunce,
          goldPurity);
      return BigDecimal.ZERO;
    }

    BigDecimal pricePerGram =
        pricePerTroyOunce.divide(TROY_OUNCE_TO_GRAMS, 10, RoundingMode.HALF_UP);
    double purity = (double) goldPurity.getNumerator() / goldPurity.getDenominator();
    BigDecimal finalPrice = pricePerGram.multiply(BigDecimal.valueOf(purity));

    return finalPrice.setScale(2, RoundingMode.HALF_UP);
  }

  public BigDecimal calculatePrice(
      BigDecimal pricePerGram, BigDecimal weight, WeightUnit weightUnit) {
    if (pricePerGram == null) {
      log.error("Price per gram is null");
      throw new GoldPriceCalculationException("Price per gram cannot be null");
    }

    if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
      log.error("Invalid weight provided: {}", weight);
      throw new GoldPriceCalculationException("Weight must be greater than zero");
    }

    if (weightUnit == null) {
      log.error("Weight unit is null");
      throw new GoldPriceCalculationException("Weight unit must be provided");
    }

    BigDecimal weightInGrams = convertWeightToGrams(weight, weightUnit);
    if (weightInGrams == null) {
      log.error("Could not convert weight to grams. Weight: {}, Unit: {}", weight, weightUnit);
      throw new GoldPriceCalculationException("Failed to convert weight to grams");
    }

    BigDecimal totalPrice = pricePerGram.multiply(weightInGrams);
    BigDecimal finalPrice = totalPrice.setScale(2, RoundingMode.HALF_UP);

    log.debug(
        "Calculated price: {} (pricePerGram={}, weightInGrams={})",
        finalPrice,
        pricePerGram,
        weightInGrams);
    return finalPrice;
  }

  private BigDecimal convertWeightToGrams(BigDecimal weight, WeightUnit weightUnit) {
    if (weight == null || weightUnit == null) {
      log.warn("Weight or weightUnit is null: weight={}, unit={}", weight, weightUnit);
      return null;
    }

    return switch (weightUnit) {
      case GRAM -> weight;
      case KILOGRAM -> weight.multiply(GRAMS_TO_KILOGRAM);
      case OUNCE -> weight.multiply(TROY_OUNCE_TO_GRAMS);
    };
  }
}
