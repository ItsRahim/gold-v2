package com.rahim.pricingservice.service.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rahim.common.exception.EntityNotFoundException;
import com.rahim.pricingservice.BaseTestConfiguration;
import com.rahim.pricingservice.dto.response.GoldTypeResponseDTO;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import com.rahim.pricingservice.service.type.impl.QueryGoldTypeService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author Rahim Ahmed
 * @created 21/05/2025
 */
class QueryGoldTypeServiceTest extends BaseTestConfiguration {
  @Mock private GoldTypeRepository goldTypeRepository;
  @InjectMocks private QueryGoldTypeService queryGoldTypeService;

  private GoldType goldType1;
  private GoldType goldType2;
  private GoldPurity purity;

  @BeforeEach
  void setup() {
    purity = new GoldPurity();
    purity.setId(1);
    purity.setLabel("22K");
    purity.setNumerator(22);
    purity.setDenominator(24);

    goldType1 = new GoldType();
    goldType1.setId(1);
    goldType1.setName("Gold Ring");
    goldType1.setPurity(purity);
    goldType1.setWeight(new BigDecimal("10.5"));
    goldType1.setUnit(WeightUnit.GRAM);
    goldType1.setPrice(new BigDecimal("550.75"));
    goldType1.setDescription("22K Gold Ring");

    goldType2 = new GoldType();
    goldType2.setId(2);
    goldType2.setName("Gold Necklace");
    goldType2.setPurity(purity);
    goldType2.setWeight(new BigDecimal("25.0"));
    goldType2.setUnit(WeightUnit.GRAM);
    goldType2.setPrice(new BigDecimal("1350.50"));
    goldType2.setDescription("22K Gold Necklace");
  }

  @Test
  void shouldReturnAllGoldTypesWithPagination() {
    List<GoldType> goldTypeList = Arrays.asList(goldType1, goldType2);
    Page<GoldType> goldTypePage = new PageImpl<>(goldTypeList);

    Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
    when(goldTypeRepository.findAll(pageable)).thenReturn(goldTypePage);

    Page<GoldTypeResponseDTO> result = queryGoldTypeService.getAllGoldTypes(0, 10);

    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).hasSize(2);

    verify(goldTypeRepository).findAll(pageable);
  }

  @Test
  void shouldReturnEmptyPageWhenNoGoldTypes() {
    Page<GoldType> emptyPage = new PageImpl<>(Collections.emptyList());
    Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
    when(goldTypeRepository.findAll(pageable)).thenReturn(emptyPage);

    Page<GoldTypeResponseDTO> result = queryGoldTypeService.getAllGoldTypes(0, 10);

    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isZero();
    assertThat(result.getContent()).isEmpty();

    verify(goldTypeRepository).findAll(pageable);
  }

  @Test
  void shouldReturnGoldTypeById() {
    when(goldTypeRepository.findById(1)).thenReturn(Optional.of(goldType1));

    GoldType result = queryGoldTypeService.getGoldTypeById(1);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getName()).isEqualTo("Gold Ring");

    verify(goldTypeRepository).findById(1);
  }

  @Test
  void shouldThrowExceptionWhenGoldTypeNotFound() {
    when(goldTypeRepository.findById(anyInt())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> queryGoldTypeService.getGoldTypeById(99))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Gold Type with ID: 99 not found");

    verify(goldTypeRepository).findById(99);
  }

  @Test
  void shouldReturnAllGoldTypesAsList() {
    List<GoldType> goldTypeList = Arrays.asList(goldType1, goldType2);
    when(goldTypeRepository.findAll()).thenReturn(goldTypeList);

    List<GoldType> result = queryGoldTypeService.getAllGoldTypes();

    assertThat(result).isNotNull().hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("Gold Ring");
    assertThat(result.get(1).getName()).isEqualTo("Gold Necklace");

    verify(goldTypeRepository).findAll();
  }

  @Test
  void shouldReturnEmptyListWhenNoGoldTypes() {
    goldTypeRepository.deleteAll();
    List<GoldType> result = queryGoldTypeService.getAllGoldTypes();

    assertThat(result).isEmpty();

    verify(goldTypeRepository).findAll();
  }

  @Test
  void shouldSaveGoldType() {
    when(goldTypeRepository.save(any(GoldType.class))).thenReturn(goldType1);

    queryGoldTypeService.saveGoldType(goldType1);

    verify(goldTypeRepository).save(goldType1);
  }

  @Test
  void shouldFindGoldTypeByName() {
    when(goldTypeRepository.findGoldTypeByNameIgnoreCase(goldType1.getName()))
        .thenReturn(Optional.of(goldType1));
    GoldTypeResponseDTO result = queryGoldTypeService.getGoldTypeByName(goldType1.getName());

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getName()).isEqualTo("Gold Ring");
  }

  @Test
  void shouldThrowExceptionWhenGoldTypeByNameNotFound() {
    when(goldTypeRepository.findById(1)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> queryGoldTypeService.getGoldTypeByName(goldType1.getName()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Gold Type with name: " + goldType1.getName() + " not found");
  }
}
