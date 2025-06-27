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
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

class QueryGoldTypeServiceTest extends BaseTestConfiguration {
  @Autowired private QueryGoldTypeService queryGoldTypeService;
  @Autowired private GoldTypeRepository goldTypeRepository;

  private GoldType goldType1;

  @BeforeEach
  void setup() {
    goldTypeRepository.deleteAll();

    GoldPurity purity =
        GoldPurity.builder().id(1).label("22K").numerator(22).denominator(24).build();

    goldType1 =
        GoldType.builder()
            .name("Gold Ring")
            .purity(purity)
            .weight(new BigDecimal("10.5"))
            .unit(WeightUnit.GRAM)
            .price(new BigDecimal("550.75"))
            .description("22K Gold Ring")
            .imageUrl("https://minio-server/bucket/gold-ring.jpg")
            .build();
    GoldType goldType2 =
        GoldType.builder()
            .name("Gold Necklace")
            .purity(purity)
            .weight(new BigDecimal("25.0"))
            .unit(WeightUnit.GRAM)
            .price(new BigDecimal("1350.50"))
            .description("22K Gold Necklace")
            .imageUrl("https://minio-server/bucket/gold-necklace.jpg")
            .build();

    goldTypeRepository.saveAll(List.of(goldType1, goldType2));
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
    assertThat(result.get(0).getName()).isEqualTo("Gold Ring");
    assertThat(result.get(1).getName()).isEqualTo("Gold Necklace");
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
            .purity(goldType1.getPurity())
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
