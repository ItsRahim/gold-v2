package com.rahim.pricingservice.service;

import com.rahim.pricingservice.dto.payload.GoldPriceUpdateDTO;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.enums.WeightUnit;
import java.math.BigDecimal;

/**
 * @created 03/05/2025
 * @author Rahim Ahmed
 */
public interface IUpdateGoldPriceService {
  void updateBasePrice(GoldPriceUpdateDTO goldPriceUpdateDTO);

  BigDecimal calculateGoldPrice(GoldPurity goldPurity, BigDecimal weight, WeightUnit weightUnit);
}
