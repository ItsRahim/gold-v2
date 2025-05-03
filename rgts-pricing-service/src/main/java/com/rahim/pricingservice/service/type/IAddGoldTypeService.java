package com.rahim.pricingservice.service.type;

import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.entity.GoldType;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
public interface IAddGoldTypeService {
  GoldType addGoldType(AddGoldTypeRequest request);
}
