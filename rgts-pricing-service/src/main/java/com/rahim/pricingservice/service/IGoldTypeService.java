package com.rahim.pricingservice.service;

import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import java.util.UUID;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
public interface IGoldTypeService {
  void addGoldType(AddGoldTypeRequest request);

  void deleteGoldTypeById(UUID id);
}
