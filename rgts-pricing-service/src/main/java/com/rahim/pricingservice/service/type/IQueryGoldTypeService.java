package com.rahim.pricingservice.service.type;

import com.rahim.pricingservice.dto.response.GoldTypeResponse;
import com.rahim.pricingservice.entity.GoldType;
import org.springframework.data.domain.Page;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
public interface IQueryGoldTypeService {
  Page<GoldTypeResponse> getAllGoldTypes(int page, int size);

  GoldType getGoldType(long id);
}
