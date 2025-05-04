package com.rahim.pricingservice.service.price;

import com.rahim.pricingservice.entity.GoldPurity;
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
  private static final BigDecimal TROY_OUNCE_TO_GRAMS = BigDecimal.valueOf(31.1035);

  public BigDecimal calculatePricePerGramForPurity(
      BigDecimal pricePerTroyOunce, GoldPurity goldPurity) {

    BigDecimal pricePerGram =
        pricePerTroyOunce.divide(
            TROY_OUNCE_TO_GRAMS,
            10,
            RoundingMode.HALF_UP);
    double purity = calculatePurityValue(goldPurity);
    BigDecimal finalPrice = pricePerGram.multiply(BigDecimal.valueOf(purity));

    return finalPrice.setScale(2, RoundingMode.HALF_UP);
  }

  private double calculatePurityValue(GoldPurity goldPurity) {
    return calculatePurity(goldPurity.getNumerator(), goldPurity.getDenominator());
  }

  private double calculatePurity(double numerator, double denominator) {
    return numerator / denominator;
  }
}
