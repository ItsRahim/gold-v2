package com.rahim.pricingservice.service.impl;

import com.rahim.cachemanager.service.RedisService;
import com.rahim.common.exception.InitialisationException;
import com.rahim.common.util.DateUtil;
import com.rahim.pricingservice.dto.payload.GoldPriceUpdateDTO;
import com.rahim.pricingservice.entity.GoldPrice;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.exception.GoldPriceCalculationException;
import com.rahim.pricingservice.repository.GoldPriceRepository;
import com.rahim.pricingservice.service.GoldPriceCalculationService;
import com.rahim.pricingservice.service.IQueryGoldPurityService;
import com.rahim.pricingservice.service.IQueryGoldTypeService;
import com.rahim.pricingservice.service.IUpdateGoldPriceService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @created 03/05/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UpdateGoldPriceService implements IUpdateGoldPriceService {
  private final GoldPriceCalculationService goldPriceCalculationService;
  private final IQueryGoldPurityService goldPurityQueryService;
  private final IQueryGoldTypeService goldTypeQueryService;
  private final GoldPriceRepository goldPriceRepository;
  private final RedisService redisService;

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
    if (goldPriceUpdateDTO == null || goldPriceUpdateDTO.getPrice() == null) {
      log.warn("Invalid update request: DTO or price is null");
      return;
    }

    BigDecimal pricePerTroyOunce = goldPriceUpdateDTO.getPrice();
    log.info("Received gold price update: {} per troy ounce", pricePerTroyOunce);

    for (GoldPurity purity : goldPurityList) {
      try {
        BigDecimal pricePerGram =
            goldPriceCalculationService.calculatePricePerGramForPurity(pricePerTroyOunce, purity);
        GoldPrice existing = goldPriceRepository.getGoldPriceByPurity(purity);

        if (existing == null) {
          createGoldPrice(purity, pricePerTroyOunce, pricePerGram);
        } else if (existing.getPurity().getLabel().equalsIgnoreCase("XAUGBP")) {
          updateExistingGoldPrice(existing, pricePerTroyOunce);
        } else {
          updateExistingGoldPrice(existing, pricePerGram);
        }
      } catch (Exception e) {
        log.error("Failed to update price for purity {}: {}", purity.getLabel(), e.getMessage(), e);
      }
    }

    log.info("Gold price update completed for {} entries", goldPurityList.size());
    updateGoldTypePrices();
  }

  @Override
  public BigDecimal calculateGoldPrice(
      GoldPurity goldPurity, BigDecimal weight, WeightUnit weightUnit) {

    validateInputs(goldPurity, weight, weightUnit);

    String carat = goldPurity.getLabel();
    Object priceObject = redisService.getValue(carat);

    if (priceObject instanceof GoldPrice goldPrice) {
      return goldPriceCalculationService.calculatePrice(goldPrice.getPrice(), weight, weightUnit);
    } else {
      throw new GoldPriceCalculationException("Gold price not available for carat: " + carat);
    }
  }

  private void validateInputs(GoldPurity goldPurity, BigDecimal weight, WeightUnit weightUnit) {
    if (goldPurity == null) {
      throw new GoldPriceCalculationException("Gold purity must be provided");
    }

    if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
      throw new GoldPriceCalculationException("Weight must be greater than zero");
    }

    if (weightUnit == null) {
      throw new GoldPriceCalculationException("Weight unit must be specified");
    }
  }

  private void createGoldPrice(
      GoldPurity goldPurity, BigDecimal pricePerTroyOunce, BigDecimal pricePerGram) {
    BigDecimal price =
        goldPurity.getLabel().equalsIgnoreCase("XAUGBP") ? pricePerTroyOunce : pricePerGram;
    GoldPrice goldPrice =
        GoldPrice.builder().purity(goldPurity).price(price).updatedAt(DateUtil.nowUtc()).build();

    goldPriceRepository.save(goldPrice);
    log.debug("New gold price created for purity: {}", goldPurity.getLabel());
    savePriceToRedis(goldPrice);
  }

  private void updateExistingGoldPrice(GoldPrice goldPrice, BigDecimal updatedPrice) {
    goldPrice.setPrice(updatedPrice);
    goldPrice.setUpdatedAt(DateUtil.nowUtc());

    goldPriceRepository.save(goldPrice);
    savePriceToRedis(goldPrice);
  }

  private void savePriceToRedis(GoldPrice goldPrice) {
    try {
      redisService.setValue(goldPrice.getPurity().getLabel(), goldPrice);
    } catch (SerializationException e) {
      log.error("Failed to add new gold price to redis: {}", e.getMessage(), e);
    }
  }

  private void updateGoldTypePrices() {
    List<GoldType> goldTypes = goldTypeQueryService.getAllGoldTypes();
    for (GoldType goldType : goldTypes) {
      BigDecimal updatedPrice =
          calculateGoldPrice(goldType.getPurity(), goldType.getWeight(), goldType.getUnit());
      goldType.setPrice(updatedPrice);
      goldTypeQueryService.saveGoldType(goldType);
    }
    log.info("Updated {} gold types", goldTypes.size());
  }
}
