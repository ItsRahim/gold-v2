package com.rahim.pricingservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.rahim.common.exception.EntityNotFoundException;
import com.rahim.pricingservice.BaseTestConfiguration;
import com.rahim.pricingservice.dto.response.GoldTypeResponseDTO;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import com.rahim.pricingservice.service.impl.QueryGoldTypeService;
import com.rahim.storageservice.service.MinioStorageService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class QueryGoldTypeServiceTest extends BaseTestConfiguration {

  @Autowired @InjectMocks private QueryGoldTypeService queryGoldTypeService;

  @Autowired private GoldTypeRepository goldTypeRepository;

  @Autowired private MinioStorageService minioStorageService;

  private GoldType goldType1;
  private GoldPurity purity;
  private MockMultipartFile imageFile;
  private final String bucketName = "bucket";

  @BeforeEach
  void setup() {
    goldTypeRepository.deleteAll();

    purity = GoldPurity.builder().id(1).label("22K").numerator(22).denominator(24).build();

    imageFile =
        new MockMultipartFile(
            "file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

    goldType1 =
        saveGoldTypeWithImage(
            "Gold Ring", new BigDecimal("10.5"), new BigDecimal("550.75"), "22K Gold Ring");
    saveGoldTypeWithImage(
        "Gold Necklace", new BigDecimal("25.0"), new BigDecimal("1350.50"), "22K Gold Necklace");
  }

  private GoldType saveGoldTypeWithImage(
      String name, BigDecimal weight, BigDecimal price, String description) {
    GoldType goldType =
        GoldType.builder()
            .name(name)
            .purity(purity)
            .weight(weight)
            .unit(WeightUnit.GRAM)
            .price(price)
            .description(description)
            .build();

    goldType = goldTypeRepository.save(goldType);

    String imageUrl =
        minioStorageService.uploadImage(bucketName, goldType.getId().toString(), imageFile);
    goldType.setImageUrl(imageUrl);

    return goldTypeRepository.save(goldType);
  }

  @Test
  void shouldReturnAllGoldTypesWithPagination() {
    Page<GoldTypeResponseDTO> result = queryGoldTypeService.getAllGoldTypes(0, 10);

    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  void shouldReturnEmptyPageWhenNoGoldTypes() {
    goldTypeRepository.deleteAll();

    Page<GoldTypeResponseDTO> result = queryGoldTypeService.getAllGoldTypes(0, 10);

    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isZero();
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldReturnGoldTypeById() {
    GoldType result = queryGoldTypeService.getGoldTypeById(goldType1.getId());

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Gold Ring");
  }

  @Test
  void shouldThrowExceptionWhenGoldTypeNotFound() {
    UUID randomId = UUID.randomUUID();

    assertThatThrownBy(() -> queryGoldTypeService.getGoldTypeById(randomId))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Gold Type with ID: " + randomId + " not found");
  }

  @Test
  void shouldReturnAllGoldTypesAsList() {
    List<GoldType> result = queryGoldTypeService.getAllGoldTypes();

    assertThat(result).isNotNull().hasSize(2);
    assertThat(result)
        .extracting(GoldType::getName)
        .containsExactlyInAnyOrder("Gold Ring", "Gold Necklace");
  }

  @Test
  void shouldReturnEmptyListWhenNoGoldTypes() {
    goldTypeRepository.deleteAll();

    List<GoldType> result = queryGoldTypeService.getAllGoldTypes();

    assertThat(result).isEmpty();
  }

  @Test
  void shouldSaveGoldType() {
    GoldType newType =
        GoldType.builder()
            .name("Gold Bracelet")
            .purity(purity)
            .weight(new BigDecimal("15.0"))
            .unit(WeightUnit.GRAM)
            .price(new BigDecimal("700.00"))
            .description("22K Gold Bracelet")
            .imageUrl("https://minio-server/bucket/gold-bracelet.jpg")
            .build();

    queryGoldTypeService.saveGoldType(newType);

    Optional<GoldType> saved = goldTypeRepository.findById(newType.getId());
    assertThat(saved).isPresent();
    assertThat(saved.get().getName()).isEqualTo("Gold Bracelet");
  }

  @Test
  void shouldFindGoldTypeByName() {
    GoldTypeResponseDTO result = queryGoldTypeService.getGoldTypeByName(goldType1.getName());

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Gold Ring");
  }

  @Test
  void shouldThrowExceptionWhenGoldTypeByNameNotFound() {
    String nonExistentName = "Nonexistent Item";

    assertThatThrownBy(() -> queryGoldTypeService.getGoldTypeByName(nonExistentName))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Gold Type with name: " + nonExistentName + " not found");
  }
}
