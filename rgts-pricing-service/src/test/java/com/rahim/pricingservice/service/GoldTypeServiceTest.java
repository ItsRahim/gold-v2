package com.rahim.pricingservice.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import com.rahim.cachemanager.service.RedisService;
import com.rahim.common.exception.BadRequestException;
import com.rahim.common.exception.DuplicateEntityException;
import com.rahim.common.exception.EntityNotFoundException;
import com.rahim.common.util.DateUtil;
import com.rahim.pricingservice.BaseTestConfiguration;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.entity.GoldPrice;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import com.rahim.pricingservice.service.impl.GoldTypeService;
import com.rahim.storageservice.service.MinioStorageService;
import com.rahim.storageservice.service.StorageServiceFactory;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class GoldTypeServiceTest extends BaseTestConfiguration {
  @Autowired @InjectMocks private GoldTypeService goldTypeService;
  @Autowired private GoldTypeRepository goldTypeRepository;
  @Mock private IUpdateGoldPriceService updateGoldPriceService;
  @Mock private RedisService redisService;
  @Mock private StorageServiceFactory storageServiceFactory;
  @Mock private MinioStorageService minioStorageService;

  private MockMultipartFile validImageFile;

  @BeforeEach
  void setUp() {
    GoldPurity goldPurity = new GoldPurity(1, "22K", 22, 24, false);
    when(redisService.getValue(anyString()))
        .thenReturn(new GoldPrice(1, goldPurity, BigDecimal.valueOf(100), DateUtil.nowUtc()));

    validImageFile =
        new MockMultipartFile(
            "file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
    when(storageServiceFactory.getStorageService()).thenReturn(minioStorageService);
  }

  @Test
  void shouldAddGoldType() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "name", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");

    GoldPurity mockPurity = GoldPurity.builder().label("22K").numerator(22).denominator(24).build();

    when(updateGoldPriceService.calculateGoldPrice(mockPurity, BigDecimal.TEN, WeightUnit.GRAM))
        .thenReturn(new BigDecimal("500.00"));

    goldTypeService.addGoldType(request, validImageFile);

    assertThat(goldTypeRepository.existsGoldTypeByNameIgnoreCase("name")).isTrue();
  }

  @Test
  void shouldThrowExceptionWhenImageFileIsNull() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "name", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");

    assertThatThrownBy(() -> goldTypeService.addGoldType(request, null))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("No image file provided");
  }

  @Test
  void shouldThrowExceptionWhenImageFileIsEmpty() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "name", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");

    MockMultipartFile emptyFile =
        new MockMultipartFile("file", "empty.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]);

    assertThatThrownBy(() -> goldTypeService.addGoldType(request, emptyFile))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("No image file provided");
  }

  @Test
  void shouldThrowExceptionWhenAddGoldTypeRequestIsNull() {
    assertThatThrownBy(() -> goldTypeService.addGoldType(null, validImageFile))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Request body cannot be null");
  }

  @Test
  void shouldThrowExceptionWhenAddGoldTypeRequestNameIsEmpty() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> goldTypeService.addGoldType(request, validImageFile))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold type name is required");
  }

  @Test
  void shouldThrowExceptionWhenAddGoldTypeRequestNameIsNull() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            null, "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> goldTypeService.addGoldType(request, validImageFile))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold type name is required");
  }

  @Test
  void shouldThrowExceptionWhenAddGoldTypeRequestNameIsExists() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "name", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");

    when(updateGoldPriceService.calculateGoldPrice(any(), any(), any()))
        .thenReturn(new BigDecimal("500.00"));

    goldTypeService.addGoldType(request, validImageFile);

    AddGoldTypeRequest request2 =
        new AddGoldTypeRequest(
            "name", "19K", BigDecimal.ONE, WeightUnit.GRAM.getValue(), "description");

    assertThatThrownBy(() -> goldTypeService.addGoldType(request2, validImageFile))
        .isInstanceOf(DuplicateEntityException.class)
        .hasMessage("Gold type already exists: " + request2.getName());

    AddGoldTypeRequest request3 =
        new AddGoldTypeRequest(
            "non-duplicate", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");

    assertThatCode(() -> goldTypeService.addGoldType(request3, validImageFile))
        .doesNotThrowAnyException();
    assertThat(goldTypeRepository.existsGoldTypeByNameIgnoreCase(request3.getName())).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"25K", "2.5K", "-12K", "K", "0K", "100K"})
  void shouldThrowExceptionWhenAddGoldTypeRequestCaratIsInvalid(String invalidCarat) {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "name", invalidCarat, BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> goldTypeService.addGoldType(request, validImageFile))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid carat label: " + request.getPurity());
  }

  @ParameterizedTest
  @ValueSource(strings = {"1K", "9K", "10K", "14K", "18K", "22K", "24K"})
  void shouldAcceptValidCaratLabels(String validCarat) {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "name-" + validCarat,
            validCarat,
            BigDecimal.TEN,
            WeightUnit.GRAM.getValue(),
            "description");

    when(updateGoldPriceService.calculateGoldPrice(any(), any(), any()))
        .thenReturn(new BigDecimal("500.00"));

    assertThatCode(() -> goldTypeService.addGoldType(request, validImageFile))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionWhenAddGoldTypeRequestWeightIsInvalid() {
    AddGoldTypeRequest request1 =
        new AddGoldTypeRequest("name1", "22K", null, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> goldTypeService.addGoldType(request1, validImageFile))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold weight must be positive");

    AddGoldTypeRequest request2 =
        new AddGoldTypeRequest(
            "name2", "22K", new BigDecimal("-1"), WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> goldTypeService.addGoldType(request2, validImageFile))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold weight must be positive");

    AddGoldTypeRequest request3 =
        new AddGoldTypeRequest(
            "name3", "22K", BigDecimal.ZERO, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> goldTypeService.addGoldType(request3, validImageFile))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold weight must be positive");
  }

  @Test
  void shouldThrowExceptionWhenAddGoldTypeRequestDescriptionIsInvalid() {
    AddGoldTypeRequest request1 =
        new AddGoldTypeRequest("name1", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), null);
    assertThatThrownBy(() -> goldTypeService.addGoldType(request1, validImageFile))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Description is required");

    AddGoldTypeRequest request2 =
        new AddGoldTypeRequest("name2", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "");
    assertThatThrownBy(() -> goldTypeService.addGoldType(request2, validImageFile))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Description is required");
  }

  @Test
  void shouldThrowExceptionWhenAddGoldTypeRequestWeightUnitIsInvalid() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("name1", "22K", BigDecimal.TEN, "ml", "description");
    assertThatThrownBy(() -> goldTypeService.addGoldType(request, validImageFile))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid weight unit: " + request.getUnit());

    AddGoldTypeRequest request2 =
        new AddGoldTypeRequest("name2", "22K", BigDecimal.TEN, null, "description");
    assertThatThrownBy(() -> goldTypeService.addGoldType(request2, validImageFile))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Weight unit is required");

    AddGoldTypeRequest request3 =
        new AddGoldTypeRequest("name3", "22K", BigDecimal.TEN, "", "description");
    assertThatThrownBy(() -> goldTypeService.addGoldType(request3, validImageFile))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Weight unit is required");
  }

  @Test
  void shouldSupportAllValidWeightUnits() {
    for (WeightUnit unit : WeightUnit.values()) {
      AddGoldTypeRequest request =
          new AddGoldTypeRequest(
              "name-" + unit.getValue(), "22K", BigDecimal.TEN, unit.getValue(), "description");

      when(updateGoldPriceService.calculateGoldPrice(any(), any(), eq(unit)))
          .thenReturn(new BigDecimal("500.00"));

      assertThatCode(() -> goldTypeService.addGoldType(request, validImageFile))
          .doesNotThrowAnyException();
    }
  }

  @Test
  void shouldDeleteGoldTypeById() {
    GoldPurity purity =
        GoldPurity.builder().id(1).label("22K").numerator(22).denominator(24).build();

    GoldType goldType =
        GoldType.builder()
            .name("Gold Ring")
            .purity(purity)
            .weight(new BigDecimal("10.5"))
            .unit(WeightUnit.GRAM)
            .price(new BigDecimal("550.75"))
            .description("22K Gold Ring")
            .imageUrl("https://minio-server/bucket/gold-ring.jpg")
            .build();

    UUID savedId = goldTypeRepository.save(goldType).getId();

    assertThat(goldTypeRepository.existsById(savedId)).isTrue();

    goldTypeService.deleteGoldTypeById(savedId);

    assertThat(goldTypeRepository.existsById(savedId)).isFalse();
  }

  @Test
  void shouldThrowExceptionWhenDeleteGoldTypeByIdNotFound() {
    UUID nonExistentId = UUID.randomUUID();

    assertThatThrownBy(() -> goldTypeService.deleteGoldTypeById(nonExistentId))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Gold Type with ID: " + nonExistentId + " not found");
  }
}
