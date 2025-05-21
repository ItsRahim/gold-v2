package com.rahim.pricingservice.service.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rahim.common.response.AbstractResponseDTO;
import com.rahim.pricingservice.BaseUnitTest;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.exception.GoldTypeNotFoundException;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.rahim.pricingservice.service.type.impl.QueryGoldTypeService;
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
public class QueryGoldTypeServiceTest extends BaseUnitTest {

  @Mock private GoldTypeRepository goldTypeRepository;

  @InjectMocks private QueryGoldTypeService queryGoldTypeService;

  private GoldType goldType1;
  private GoldType goldType2;
  private GoldPurity purity;

  @BeforeEach
  public void setup() {
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
  public void shouldReturnAllGoldTypesWithPagination() {
    // Given
    List<GoldType> goldTypeList = Arrays.asList(goldType1, goldType2);
    Page<GoldType> goldTypePage = new PageImpl<>(goldTypeList);

    Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
    when(goldTypeRepository.findAll(eq(pageable))).thenReturn(goldTypePage);

    // When
    Page<AbstractResponseDTO> result = queryGoldTypeService.getAllGoldTypes(0, 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).hasSize(2);

    // Verify the repository was called with correct parameters
    verify(goldTypeRepository).findAll(eq(pageable));
  }

  @Test
  public void shouldReturnEmptyPageWhenNoGoldTypes() {
    // Given
    Page<GoldType> emptyPage = new PageImpl<>(Collections.emptyList());
    Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
    when(goldTypeRepository.findAll(eq(pageable))).thenReturn(emptyPage);

    // When
    Page<AbstractResponseDTO> result = queryGoldTypeService.getAllGoldTypes(0, 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(0);
    assertThat(result.getContent()).isEmpty();

    // Verify the repository was called with correct parameters
    verify(goldTypeRepository).findAll(eq(pageable));
  }

  @Test
  public void shouldReturnGoldTypeById() {
    // Given
    when(goldTypeRepository.findById(1L)).thenReturn(Optional.of(goldType1));

    // When
    GoldType result = queryGoldTypeService.getGoldTypeById(1L);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getName()).isEqualTo("Gold Ring");

    // Verify the repository was called
    verify(goldTypeRepository).findById(1L);
  }

  @Test
  public void shouldThrowExceptionWhenGoldTypeNotFound() {
    // Given
    when(goldTypeRepository.findById(anyLong())).thenReturn(Optional.empty());

    // When/Then
    assertThatThrownBy(() -> queryGoldTypeService.getGoldTypeById(99L))
        .isInstanceOf(GoldTypeNotFoundException.class)
        .hasMessage("Gold Type with ID: 99 not found");

    // Verify the repository was called
    verify(goldTypeRepository).findById(99L);
  }

  @Test
  public void shouldReturnAllGoldTypesAsList() {
    // Given
    List<GoldType> goldTypeList = Arrays.asList(goldType1, goldType2);
    when(goldTypeRepository.findAll()).thenReturn(goldTypeList);

    // When
    List<GoldType> result = queryGoldTypeService.getAllGoldTypes();

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("Gold Ring");
    assertThat(result.get(1).getName()).isEqualTo("Gold Necklace");

    // Verify the repository was called
    verify(goldTypeRepository).findAll();
  }

  @Test
  public void shouldReturnEmptyListWhenNoGoldTypes() {
    // Given
    when(goldTypeRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<GoldType> result = queryGoldTypeService.getAllGoldTypes();

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    // Verify the repository was called
    verify(goldTypeRepository).findAll();
  }

  @Test
  public void shouldSaveGoldType() {
    // Given
    when(goldTypeRepository.save(any(GoldType.class))).thenReturn(goldType1);

    // When
    queryGoldTypeService.saveGoldType(goldType1);

    // Then
    verify(goldTypeRepository).save(eq(goldType1));
  }
}
