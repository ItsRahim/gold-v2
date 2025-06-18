package com.rahim.pricingservice.service;

import com.rahim.pricingservice.entity.GoldPurity;
import java.util.List;

/**
 * @created 03/05/2025
 * @author Rahim Ahmed
 */
public interface IGoldPurityQueryService {
  GoldPurity getGoldPurityByCaratLabel(String label);

  List<GoldPurity> getAllGoldPurities();
}
