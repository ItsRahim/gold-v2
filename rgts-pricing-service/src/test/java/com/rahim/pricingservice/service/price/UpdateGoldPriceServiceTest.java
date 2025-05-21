package com.rahim.pricingservice.service.price;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.rahim.cachemanager.service.RedisService;
import com.rahim.common.exception.InitialisationException;
import com.rahim.pricingservice.dto.payload.GoldPriceUpdateDTO;
import com.rahim.pricingservice.entity.GoldPrice;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.exception.GoldPriceCalculationException;
import com.rahim.pricingservice.repository.GoldPriceRepository;
import com.rahim.pricingservice.service.price.impl.UpdateGoldPriceService;
import com.rahim.pricingservice.service.purity.IGoldPurityQueryService;
import com.rahim.pricingservice.service.type.IQueryGoldTypeService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UpdateGoldPriceServiceTest {

  @Mock private GoldPriceCalculationService goldPriceCalculationService;

  @Mock private IGoldPurityQueryService goldPurityQueryService;

  @Mock private IQueryGoldTypeService goldTypeQueryService;

  @Mock private GoldPriceRepository goldPriceRepository;

  @Mock private RedisService redisService;

  @InjectMocks private UpdateGoldPriceService updateGoldPriceService;

  private GoldPurity goldPurity24K;
  private GoldPurity goldPurityXAUGBP;
  private GoldPriceUpdateDTO goldPriceUpdateDTO;
  private GoldPrice existingGoldPrice;
  private GoldType goldType;

  @BeforeEach
  void setUp() {
    goldPurity24K = GoldPurity.builder().id(1).label("24K").numerator(24).denominator(24).build();

    goldPurityXAUGBP =
        GoldPurity.builder().id(2).label("XAUGBP").numerator(24).denominator(24).build();

    goldPriceUpdateDTO = GoldPriceUpdateDTO.builder().price(new BigDecimal("2000.00")).build();

    existingGoldPrice =
        GoldPrice.builder()
            .id(1)
            .purity(goldPurity24K)
            .price(new BigDecimal("1950.00"))
            .updatedAt(Instant.now())
            .build();

    goldType =
        GoldType.builder()
            .id(1)
            .purity(goldPurity24K)
            .weight(new BigDecimal("10.0"))
            .unit(WeightUnit.GRAM)
            .price(new BigDecimal("500.00"))
            .build();
  }

  @Test
  void init_ShouldInitializeGoldPurityList_WhenSuccessful() {
    List<GoldPurity> expectedPurities = Arrays.asList(goldPurity24K, goldPurityXAUGBP);
    when(goldPurityQueryService.getAllGoldPurities()).thenReturn(expectedPurities);

    updateGoldPriceService.init();

    verify(goldPurityQueryService).getAllGoldPurities();
    List<GoldPurity> actualPurities =
        (List<GoldPurity>) ReflectionTestUtils.getField(updateGoldPriceService, "goldPurityList");
    assertEquals(expectedPurities, actualPurities);
  }

  @Test
  void init_ShouldThrowInitialisationException_WhenExceptionOccurs() {
    when(goldPurityQueryService.getAllGoldPurities())
        .thenThrow(new RuntimeException("Database error"));

    InitialisationException exception =
        assertThrows(InitialisationException.class, () -> updateGoldPriceService.init());
    assertEquals("Failed to initialize the system's gold purity data", exception.getMessage());
  }

  @Test
  void updateBasePrice_ShouldReturnEarly_WhenDTOIsNull() {
    updateGoldPriceService.updateBasePrice(null);

    verifyNoInteractions(goldPriceCalculationService, goldPriceRepository, redisService);
  }

  @Test
  void updateBasePrice_ShouldReturnEarly_WhenPriceIsNull() {
    GoldPriceUpdateDTO nullPriceDTO = GoldPriceUpdateDTO.builder().price(null).build();

    updateGoldPriceService.updateBasePrice(nullPriceDTO);

    verifyNoInteractions(goldPriceCalculationService, goldPriceRepository, redisService);
  }

  @Test
  void updateBasePrice_ShouldHandleXAUGBPPurity_WithTroyOuncePrice() {
    ReflectionTestUtils.setField(
        updateGoldPriceService, "goldPurityList", Collections.singletonList(goldPurityXAUGBP));

    when(goldPriceCalculationService.calculatePricePerGramForPurity(any(), eq(goldPurityXAUGBP)))
        .thenReturn(new BigDecimal("64.37"));
    when(goldPriceRepository.getGoldPriceByPurity(goldPurityXAUGBP)).thenReturn(null);
    when(goldTypeQueryService.getAllGoldTypes()).thenReturn(List.of());

    updateGoldPriceService.updateBasePrice(goldPriceUpdateDTO);

    verify(goldPriceRepository)
        .save(
            argThat(
                goldPrice ->
                    goldPrice.getPurity().equals(goldPurityXAUGBP)
                        && goldPrice.getPrice().equals(new BigDecimal("2000.00"))));
  }

  @Test
  void updateBasePrice_ShouldContinueProcessing_WhenOneUpdateFails() {
    GoldPurity secondPurity = GoldPurity.builder().id(3).label("22K").build();
    ReflectionTestUtils.setField(
        updateGoldPriceService, "goldPurityList", Arrays.asList(goldPurity24K, secondPurity));

    when(goldPriceCalculationService.calculatePricePerGramForPurity(any(), eq(goldPurity24K)))
        .thenThrow(new RuntimeException("Calculation failed"));
    when(goldPriceCalculationService.calculatePricePerGramForPurity(any(), eq(secondPurity)))
        .thenReturn(new BigDecimal("59.54"));
    when(goldPriceRepository.getGoldPriceByPurity(secondPurity)).thenReturn(null);
    when(goldTypeQueryService.getAllGoldTypes()).thenReturn(List.of());

    updateGoldPriceService.updateBasePrice(goldPriceUpdateDTO);

    verify(goldPriceRepository, never()).save(argThat(gp -> gp.getPurity().equals(goldPurity24K)));
    verify(goldPriceRepository).save(argThat(gp -> gp.getPurity().equals(secondPurity)));
  }

  @Test
  void updateBasePrice_ShouldHandleRedisSerializationException() {
    ReflectionTestUtils.setField(
        updateGoldPriceService, "goldPurityList", Collections.singletonList(goldPurity24K));

    when(goldPriceCalculationService.calculatePricePerGramForPurity(any(), eq(goldPurity24K)))
        .thenReturn(new BigDecimal("64.37"));
    when(goldPriceRepository.getGoldPriceByPurity(goldPurity24K)).thenReturn(null);
    when(goldTypeQueryService.getAllGoldTypes()).thenReturn(List.of());
    doThrow(new SerializationException("Redis error"))
        .when(redisService)
        .setValue(anyString(), any());

    assertDoesNotThrow(() -> updateGoldPriceService.updateBasePrice(goldPriceUpdateDTO));

    verify(goldPriceRepository).save(any(GoldPrice.class));
    verify(redisService).setValue(anyString(), any(GoldPrice.class));
  }

  @Test
  void calculateGoldPrice_ShouldReturnCalculatedPrice_WhenValidInputsAndPriceExists() {
    BigDecimal weight = new BigDecimal("10.0");
    WeightUnit weightUnit = WeightUnit.GRAM;
    BigDecimal expectedPrice = new BigDecimal("643.70");

    when(redisService.getValue("24K")).thenReturn(existingGoldPrice);
    when(goldPriceCalculationService.calculatePrice(
            existingGoldPrice.getPrice(), weight, weightUnit))
        .thenReturn(expectedPrice);

    BigDecimal result =
        updateGoldPriceService.calculateGoldPrice(goldPurity24K, weight, weightUnit);

    assertEquals(expectedPrice, result);
    verify(redisService).getValue("24K");
    verify(goldPriceCalculationService)
        .calculatePrice(existingGoldPrice.getPrice(), weight, weightUnit);
  }

  @Test
  void calculateGoldPrice_ShouldThrowException_WhenGoldPurityIsNull() {
    BigDecimal weight = new BigDecimal("10.0");
    WeightUnit weightUnit = WeightUnit.GRAM;

    GoldPriceCalculationException exception =
        assertThrows(
            GoldPriceCalculationException.class,
            () -> updateGoldPriceService.calculateGoldPrice(null, weight, weightUnit));
    assertEquals("Gold purity must be provided", exception.getMessage());
  }

  @Test
  void calculateGoldPrice_ShouldThrowException_WhenWeightIsNull() {
    GoldPriceCalculationException exception =
        assertThrows(
            GoldPriceCalculationException.class,
            () -> updateGoldPriceService.calculateGoldPrice(goldPurity24K, null, WeightUnit.GRAM));
    assertEquals("Weight must be greater than zero", exception.getMessage());
  }

  @Test
  void calculateGoldPrice_ShouldThrowException_WhenWeightIsZero() {
    GoldPriceCalculationException exception =
        assertThrows(
            GoldPriceCalculationException.class,
            () ->
                updateGoldPriceService.calculateGoldPrice(
                    goldPurity24K, BigDecimal.ZERO, WeightUnit.GRAM));
    assertEquals("Weight must be greater than zero", exception.getMessage());
  }

  @Test
  void calculateGoldPrice_ShouldThrowException_WhenWeightIsNegative() {
    GoldPriceCalculationException exception =
        assertThrows(
            GoldPriceCalculationException.class,
            () ->
                updateGoldPriceService.calculateGoldPrice(
                    goldPurity24K, new BigDecimal("-1.0"), WeightUnit.GRAM));
    assertEquals("Weight must be greater than zero", exception.getMessage());
  }

  @Test
  void calculateGoldPrice_ShouldThrowException_WhenWeightUnitIsNull() {
    GoldPriceCalculationException exception =
        assertThrows(
            GoldPriceCalculationException.class,
            () ->
                updateGoldPriceService.calculateGoldPrice(
                    goldPurity24K, new BigDecimal("10.0"), null));
    assertEquals("Weight unit must be specified", exception.getMessage());
  }

  @Test
  void calculateGoldPrice_ShouldThrowException_WhenPriceNotFoundInRedis() {
    when(redisService.getValue("24K")).thenReturn(null);

    GoldPriceCalculationException exception =
        assertThrows(
            GoldPriceCalculationException.class,
            () ->
                updateGoldPriceService.calculateGoldPrice(
                    goldPurity24K, new BigDecimal("10.0"), WeightUnit.GRAM));
    assertEquals("Gold price not available for carat: 24K", exception.getMessage());
  }

  @Test
  void calculateGoldPrice_ShouldThrowException_WhenRedisReturnsInvalidObject() {
    when(redisService.getValue("24K")).thenReturn("Invalid object");

    GoldPriceCalculationException exception =
        assertThrows(
            GoldPriceCalculationException.class,
            () ->
                updateGoldPriceService.calculateGoldPrice(
                    goldPurity24K, new BigDecimal("10.0"), WeightUnit.GRAM));
    assertEquals("Gold price not available for carat: 24K", exception.getMessage());
  }

  @Test
  void updateBasePrice_ShouldHandleEmptyGoldPurityList() {
    ReflectionTestUtils.setField(updateGoldPriceService, "goldPurityList", List.of());
    when(goldTypeQueryService.getAllGoldTypes()).thenReturn(List.of());

    updateGoldPriceService.updateBasePrice(goldPriceUpdateDTO);

    verifyNoInteractions(goldPriceCalculationService, goldPriceRepository, redisService);
    verify(goldTypeQueryService).getAllGoldTypes();
  }
}
