package com.rahim.pricingservice.service;

import static org.junit.jupiter.api.Assertions.*;

import com.rahim.cachemanager.service.RedisService;
import com.rahim.pricingservice.BaseTestConfiguration;
import com.rahim.pricingservice.dto.payload.GoldPriceUpdateDTO;
import com.rahim.pricingservice.entity.GoldPrice;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.exception.GoldPriceCalculationException;
import com.rahim.pricingservice.repository.GoldPriceRepository;
import com.rahim.pricingservice.repository.GoldPurityRepository;
import com.rahim.pricingservice.service.impl.UpdateGoldPriceService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

class UpdateGoldPriceServiceTest extends BaseTestConfiguration {
  @Autowired private UpdateGoldPriceService updateGoldPriceService;
  @Autowired private RedisService redisService;
  @Autowired private GoldPriceRepository goldPriceRepository;
  @Autowired private GoldPurityRepository goldPurityRepository;

  private GoldPurity goldPurity24K;
  private GoldPurity goldPurityXAUGBP;
  private GoldPriceUpdateDTO goldPriceUpdateDTO;

  @BeforeEach
  void setUp() {
    goldPurity24K = goldPurityRepository.save(new GoldPurity(1, "24K", 24, 24, false));
    goldPurityXAUGBP = goldPurityRepository.save(new GoldPurity(2, "XAUGBP", 24, 24, false));
    goldPriceUpdateDTO = GoldPriceUpdateDTO.builder().price(new BigDecimal("2000.00")).build();
  }

  @Test
  void init_ShouldInitializeGoldPurityList_WhenSuccessful() {
    List<GoldPurity> purities =
        (List<GoldPurity>) ReflectionTestUtils.getField(updateGoldPriceService, "goldPurityList");

    assertNotNull(purities);
    assertFalse(purities.isEmpty());
  }

  @Test
  void updateBasePrice_ShouldHandleXAUGBPPurity_WithTroyOuncePrice() {
    ReflectionTestUtils.setField(
        updateGoldPriceService, "goldPurityList", List.of(goldPurityXAUGBP));

    updateGoldPriceService.updateBasePrice(goldPriceUpdateDTO);

    GoldPrice saved = goldPriceRepository.getGoldPriceByPurity(goldPurityXAUGBP);
    assertNotNull(saved);
    assertEquals(new BigDecimal("2000.00"), saved.getPrice());

    Object cached = redisService.getValue("XAUGBP");
    assertNotNull(cached);
    assertInstanceOf(GoldPrice.class, cached);
  }

  @Test
  void updateBasePrice_ShouldHandleEmptyGoldPurityList() {
    ReflectionTestUtils.setField(updateGoldPriceService, "goldPurityList", List.of());
    assertDoesNotThrow(() -> updateGoldPriceService.updateBasePrice(goldPriceUpdateDTO));
  }

  @Test
  void calculateGoldPrice_ShouldThrowException_WhenGoldPurityIsNull() {
    assertThrows(
        GoldPriceCalculationException.class,
        () -> updateGoldPriceService.calculateGoldPrice(null, BigDecimal.ONE, WeightUnit.GRAM));
  }

  @Test
  void calculateGoldPrice_ShouldThrowException_WhenWeightIsNull() {
    assertThrows(
        GoldPriceCalculationException.class,
        () -> updateGoldPriceService.calculateGoldPrice(goldPurity24K, null, WeightUnit.GRAM));
  }

  @Test
  void calculateGoldPrice_ShouldThrowException_WhenWeightIsZero() {
    assertThrows(
        GoldPriceCalculationException.class,
        () ->
            updateGoldPriceService.calculateGoldPrice(
                goldPurity24K, BigDecimal.ZERO, WeightUnit.GRAM));
  }

  @Test
  void calculateGoldPrice_ShouldThrowException_WhenWeightIsNegative() {
    assertThrows(
        GoldPriceCalculationException.class,
        () ->
            updateGoldPriceService.calculateGoldPrice(
                goldPurity24K, new BigDecimal("-1"), WeightUnit.GRAM));
  }

  @Test
  void calculateGoldPrice_ShouldThrowException_WhenWeightUnitIsNull() {
    assertThrows(
        GoldPriceCalculationException.class,
        () -> updateGoldPriceService.calculateGoldPrice(goldPurity24K, BigDecimal.TEN, null));
  }

  @Test
  void calculateGoldPrice_ShouldThrowException_WhenPriceNotFoundInRedis() {
    redisService.deleteKey("24K");

    assertThrows(
        GoldPriceCalculationException.class,
        () ->
            updateGoldPriceService.calculateGoldPrice(
                goldPurity24K, BigDecimal.ONE, WeightUnit.GRAM));
  }

  @Test
  void calculateGoldPrice_ShouldThrowException_WhenRedisReturnsInvalidObject() {
    redisService.setValue("24K", UUID.randomUUID().toString());

    assertThrows(
        GoldPriceCalculationException.class,
        () ->
            updateGoldPriceService.calculateGoldPrice(
                goldPurity24K, BigDecimal.ONE, WeightUnit.GRAM));
  }
}
