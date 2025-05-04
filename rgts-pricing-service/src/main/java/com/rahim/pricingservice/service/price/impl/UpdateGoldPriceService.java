package com.rahim.pricingservice.service.price.impl;

import com.rahim.common.exception.InitialisationException;
import com.rahim.common.util.DateUtil;
import com.rahim.pricingservice.dto.payload.GoldPriceUpdateDTO;
import com.rahim.pricingservice.entity.GoldPrice;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.repository.GoldPriceRepository;
import com.rahim.pricingservice.service.price.GoldPriceCalculationService;
import com.rahim.pricingservice.service.price.IUpdateGoldPriceService;
import com.rahim.pricingservice.service.purity.IGoldPurityQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @created 03/05/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UpdateGoldPriceService implements IUpdateGoldPriceService {

  private final GoldPriceRepository goldPriceRepository;
  private final IGoldPurityQueryService goldPurityQueryService;
  private final GoldPriceCalculationService goldPriceCalculationService;

  private List<GoldPurity> goldPurityList = new ArrayList<>();

  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    try {
      log.info("Initialising gold purity data with labels...");
      goldPurityList = goldPurityQueryService.getAllGoldPurities();
      log.info("Gold purity data successfully initialised with {} entries.", goldPurityList.size());
    } catch (Exception e) {
      log.error(
          "An error occurred while initialising the gold purity data with labels: {}",
          e.getMessage(),
          e);
      throw new InitialisationException("Failed to initialize the system's gold purity data");
    }
  }

  @Override
  public void updateBasePrice(GoldPriceUpdateDTO goldPriceUpdateDTO) {
    BigDecimal pricePerTroyOunce = goldPriceUpdateDTO.getPrice();
    log.info("Received gold price update: {} per troy ounce", pricePerTroyOunce);

    try {
      for (GoldPurity goldPurity : goldPurityList) {
        GoldPrice goldPrice = goldPriceRepository.getGoldPriceByPurity(goldPurity);

        BigDecimal pricePerGram =
            goldPriceCalculationService.calculatePricePerGramForPurity(
                pricePerTroyOunce, goldPurity);
        if (goldPrice == null) {
          createGoldPrice(goldPurity, pricePerGram);
        } else {
          updateExistingGoldPrice(goldPrice, pricePerGram);
        }
      }
      log.info("Gold price update successful for {} entries", goldPurityList.size());
    } catch (Exception e) {
      log.error("Error occurred while updating gold prices", e);
    }
  }

  private void createGoldPrice(GoldPurity goldPurity, BigDecimal pricePerGram) {
    GoldPrice newGoldPrice =
        GoldPrice.builder()
            .purity(goldPurity)
            .price(pricePerGram)
            .updatedAt(DateUtil.generateInstant())
            .build();

    goldPriceRepository.save(newGoldPrice);
    log.debug("New gold price created for purity: {}", goldPurity.getLabel());
  }

  private void updateExistingGoldPrice(GoldPrice goldPrice, BigDecimal updatedPrice) {
    goldPrice.setPrice(updatedPrice);
    goldPrice.setUpdatedAt(DateUtil.generateInstant());

    goldPriceRepository.save(goldPrice);
  }
}
