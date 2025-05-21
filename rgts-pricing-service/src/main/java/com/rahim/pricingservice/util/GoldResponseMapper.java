package com.rahim.pricingservice.util;

import com.rahim.common.exception.MappingException;
import com.rahim.common.response.AbstractResponseDTO;
import com.rahim.pricingservice.dto.response.GoldTypeResponseDTO;
import com.rahim.pricingservice.entity.GoldType;
import lombok.extern.slf4j.Slf4j;

/**
 * @created 04/05/2025
 * @author Rahim Ahmed
 */
@Slf4j
public class GoldResponseMapper {
  private GoldResponseMapper() {}

  public static AbstractResponseDTO map(Object entity) {
    if (entity == null) {
      throw new MappingException("Entity must not be null");
    }

    return switch (entity) {
      case GoldType goldType -> mapGoldType(goldType);
      default -> throw new MappingException("No mapping defined for class: " + entity.getClass());
    };
  }

  private static GoldTypeResponseDTO mapGoldType(GoldType goldType) {
    return GoldTypeResponseDTO.builder()
        .id(goldType.getId())
        .name(goldType.getName())
        .purity(goldType.getPurity().getLabel())
        .weight(goldType.getWeight())
        .unit(goldType.getUnit())
        .description(goldType.getDescription())
        .price(goldType.getPrice())
        .build();
  }
}
