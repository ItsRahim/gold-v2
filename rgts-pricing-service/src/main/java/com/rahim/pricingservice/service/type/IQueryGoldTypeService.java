package com.rahim.pricingservice.service.type;

import com.rahim.common.response.AbstractResponseDTO;
import com.rahim.pricingservice.entity.GoldType;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
public interface IQueryGoldTypeService {
  Page<AbstractResponseDTO> getAllGoldTypes(int page, int size);

  GoldType getGoldTypeById(long id);

  List<GoldType> getAllGoldTypes();

  void saveGoldType(GoldType goldType);
}
