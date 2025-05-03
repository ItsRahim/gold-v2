package com.rahim.pricingservice.service;

import com.rahim.pricingservice.entity.GoldType;
import org.springframework.data.domain.Page;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
public interface IQueryGoldTypeService {
  Page<GoldType> getAllGoldTypes(int page, int size);

  GoldType getGoldType(long id);
}
