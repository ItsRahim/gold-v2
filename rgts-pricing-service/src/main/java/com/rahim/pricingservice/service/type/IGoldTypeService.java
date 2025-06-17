package com.rahim.pricingservice.service.type;

import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
public interface IGoldTypeService {
  void addGoldType(AddGoldTypeRequest request);

  void deleteGoldTypeById(int id);
}
