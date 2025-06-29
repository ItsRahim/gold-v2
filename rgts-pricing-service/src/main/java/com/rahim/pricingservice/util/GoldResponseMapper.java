package com.rahim.pricingservice.util;

import com.rahim.common.exception.MappingException;
import com.rahim.pricingservice.dto.response.GoldTypeResponseDTO;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.storageservice.service.StorageService;
import com.rahim.storageservice.service.StorageServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @created 04/05/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Component
public final class GoldResponseMapper {
  private final StorageService storageService;

  public GoldResponseMapper(StorageServiceFactory factory) {
    this.storageService = factory.getStorageService();
  }

  public GoldTypeResponseDTO mapToGoldType(GoldType goldType) {
    try {
      String imageUrl = storageService.getPresignedUrl(goldType.getImageUrl());
      return GoldTypeResponseDTO.builder()
          .id(goldType.getId())
          .name(goldType.getName())
          .purity(extractPurity(goldType))
          .weight(formatWeight(goldType))
          .description(goldType.getDescription())
          .price(goldType.getPrice())
          .imageUrl(imageUrl)
          .build();
    } catch (Exception e) {
      log.error("Error mapping GoldType entity to DTO: {}", e.getMessage(), e);
      throw new MappingException("Failed to map GoldType entity", e);
    }
  }

  private static String extractPurity(GoldType goldType) {
    return goldType.getPurity() != null ? goldType.getPurity().getLabel() : null;
  }

  private static String formatWeight(GoldType goldType) {
    if (goldType.getWeight() == null || goldType.getUnit() == null) {
      return null;
    }
    return goldType.getWeight().toPlainString() + " " + goldType.getUnit().getValue();
  }
}
