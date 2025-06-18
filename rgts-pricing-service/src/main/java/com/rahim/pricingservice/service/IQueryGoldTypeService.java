package com.rahim.pricingservice.service;

import com.rahim.pricingservice.dto.response.GoldTypeResponseDTO;
import com.rahim.pricingservice.entity.GoldType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
public interface IQueryGoldTypeService {
  Page<GoldTypeResponseDTO> getAllGoldTypes(int page, int size);

  GoldType getGoldTypeById(UUID id);

  GoldTypeResponseDTO getGoldTypeByName(String name);

  List<GoldType> getAllGoldTypes();

  void saveGoldType(GoldType goldType);
}
