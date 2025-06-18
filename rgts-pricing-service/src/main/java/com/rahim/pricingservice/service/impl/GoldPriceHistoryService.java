package com.rahim.pricingservice.service.impl;

import com.rahim.pricingservice.entity.GoldPrice;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.entity.PriceHistory;
import com.rahim.pricingservice.repository.GoldPriceHistoryRepository;
import com.rahim.pricingservice.repository.GoldPriceRepository;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @created 14/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoldPriceHistoryService {

  private final GoldPriceHistoryRepository goldPriceHistoryRepository;
  private final GoldPriceRepository goldPriceRepository;
  private final GoldTypeRepository goldTypeRepository;

  @Scheduled(cron = "0 59 23 * * *")
  @Transactional
  public void archiveDailyGoldPrices() {
    log.info("Starting scheduled gold price archival task");

    try {
      List<GoldPrice> currentGoldPrices = goldPriceRepository.findAll();

      if (currentGoldPrices.isEmpty()) {
        log.warn("No gold prices found to archive");
        return;
      }

      List<GoldType> allGoldTypes = goldTypeRepository.findAll();
      Map<Integer, GoldType> goldTypeMap =
          allGoldTypes.stream().collect(Collectors.toMap(GoldType::getId, goldType -> goldType));

      List<PriceHistory> priceHistories =
          currentGoldPrices.stream()
              .map(goldPrice -> createPriceHistory(goldPrice, goldTypeMap))
              .collect(Collectors.toList());

      goldPriceHistoryRepository.saveAll(priceHistories);

      log.info("Successfully archived {} gold price records", priceHistories.size());

    } catch (Exception e) {
      log.error("Failed to archive gold prices", e);
      throw new RuntimeException("Gold price archival failed", e);
    }
  }

  private PriceHistory createPriceHistory(GoldPrice goldPrice, Map<Integer, GoldType> goldTypeMap) {
    GoldType goldType = goldTypeMap.get(goldPrice.getId());

    if (goldType == null) {
      String errorMsg =
          String.format("GoldType not found for gold price ID: %d", goldPrice.getId());
      log.error(errorMsg);
      throw new RuntimeException(errorMsg);
    }

    return PriceHistory.builder()
        .goldType(goldType)
        .price(goldPrice.getPrice())
        .updatedAt(goldPrice.getUpdatedAt())
        .build();
  }
}
