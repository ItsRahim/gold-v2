package com.rahim.pricingservice.service.type;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import com.rahim.common.exception.BadRequestException;
import com.rahim.common.exception.DuplicateEntityException;
import com.rahim.common.exception.ServiceException;
import com.rahim.pricingservice.BaseUnitTest;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.exception.GoldPriceCalculationException;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import com.rahim.pricingservice.service.price.IUpdateGoldPriceService;
import com.rahim.pricingservice.service.purity.IGoldPurityQueryService;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
public class AddGoldTypeServiceTest extends BaseUnitTest {

  @Autowired private IAddGoldTypeService addGoldTypeService;

  @Autowired private GoldTypeRepository goldTypeRepository;

  @Mock private IGoldPurityQueryService goldPurityQueryService;

  @Mock private IUpdateGoldPriceService updateGoldPriceService;

  @Test
  public void shouldAddGoldType() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "name", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");

    GoldPurity mockPurity = new GoldPurity();
    mockPurity.setLabel("22K");
    mockPurity.setNumerator(22);
    mockPurity.setDenominator(24);

    when(goldPurityQueryService.getGoldPurityByCaratLabel("22K")).thenReturn(mockPurity);
    when(updateGoldPriceService.calculateGoldPrice(
            eq(mockPurity), eq(BigDecimal.TEN), eq(WeightUnit.GRAM)))
        .thenReturn(new BigDecimal("500.00"));

    addGoldTypeService.addGoldType(request);

    assertThat(goldTypeRepository.existsGoldTypeByNameIgnoreCase("name")).isTrue();
  }

  @Test
  public void shouldThrowExceptionWhenAddGoldTypeRequestIsNull() {
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Request body cannot be null");
  }

  @Test
  public void shouldThrowExceptionWhenAddGoldTypeRequestNameIsEmpty() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold type name is required");
  }

  @Test
  public void shouldThrowExceptionWhenAddGoldTypeRequestNameIsNull() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            null, "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold type name is required");
  }

  @Test
  public void shouldThrowExceptionWhenAddGoldTypeRequestNameIsExists() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "name", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");

    GoldPurity mockPurity = new GoldPurity();
    mockPurity.setLabel("22K");
    when(goldPurityQueryService.getGoldPurityByCaratLabel("22K")).thenReturn(mockPurity);
    when(updateGoldPriceService.calculateGoldPrice(any(), any(), any()))
        .thenReturn(new BigDecimal("500.00"));

    addGoldTypeService.addGoldType(request);

    AddGoldTypeRequest request2 =
        new AddGoldTypeRequest(
            "name", "19K", BigDecimal.ONE, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request2))
        .isInstanceOf(DuplicateEntityException.class)
        .hasMessage("Gold type already exists: " + request2.getName());

    AddGoldTypeRequest request3 =
        new AddGoldTypeRequest(
            "non-duplicate", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");

    assertThatCode(() -> addGoldTypeService.addGoldType(request3)).doesNotThrowAnyException();
    assertThat(goldTypeRepository.existsGoldTypeByNameIgnoreCase(request3.getName())).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"25K", "2.5K", "-12K", "K", "0K", "100K"})
  public void shouldThrowExceptionWhenAddGoldTypeRequestCaratIsInvalid(String invalidCarat) {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "name", invalidCarat, BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid carat label: " + request.getCaratLabel());
  }

  @ParameterizedTest
  @ValueSource(strings = {"1K", "9K", "10K", "14K", "18K", "22K", "24K"})
  public void shouldAcceptValidCaratLabels(String validCarat) {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "name-" + validCarat,
            validCarat,
            BigDecimal.TEN,
            WeightUnit.GRAM.getValue(),
            "description");

    GoldPurity mockPurity = new GoldPurity();
    mockPurity.setLabel(validCarat);

    when(goldPurityQueryService.getGoldPurityByCaratLabel(validCarat)).thenReturn(mockPurity);
    when(updateGoldPriceService.calculateGoldPrice(any(), any(), any()))
        .thenReturn(new BigDecimal("500.00"));

    assertThatCode(() -> addGoldTypeService.addGoldType(request)).doesNotThrowAnyException();
  }

  @Test
  public void shouldThrowExceptionWhenAddGoldTypeRequestWeightIsInvalid() {
    AddGoldTypeRequest request1 =
        new AddGoldTypeRequest("name1", "22K", null, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request1))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold weight must be positive");

    AddGoldTypeRequest request2 =
        new AddGoldTypeRequest(
            "name2", "22K", new BigDecimal("-1"), WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request2))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold weight must be positive");

    AddGoldTypeRequest request3 =
        new AddGoldTypeRequest(
            "name3", "22K", BigDecimal.ZERO, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request3))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold weight must be positive");
  }

  @Test
  public void shouldThrowExceptionWhenAddGoldTypeRequestDescriptionIsInvalid() {
    AddGoldTypeRequest request1 =
        new AddGoldTypeRequest("name1", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), null);
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request1))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Description is required");

    AddGoldTypeRequest request2 =
        new AddGoldTypeRequest("name2", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request2))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Description is required");
  }

  @Test
  public void shouldThrowExceptionWhenAddGoldTypeRequestWeightUnitIsInvalid() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("name1", "22K", BigDecimal.TEN, "ml", "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid weight unit: " + request.getUnit());

    AddGoldTypeRequest request2 =
        new AddGoldTypeRequest("name2", "22K", BigDecimal.TEN, null, "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request2))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Weight unit is required");

    AddGoldTypeRequest request3 =
        new AddGoldTypeRequest("name3", "22K", BigDecimal.TEN, "", "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request3))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Weight unit is required");
  }

  @Test
  public void shouldSupportAllValidWeightUnits() {
    for (WeightUnit unit : WeightUnit.values()) {
      AddGoldTypeRequest request =
          new AddGoldTypeRequest(
              "name-" + unit.getValue(), "22K", BigDecimal.TEN, unit.getValue(), "description");

      GoldPurity mockPurity = new GoldPurity();
      mockPurity.setLabel("22K");

      when(goldPurityQueryService.getGoldPurityByCaratLabel("22K")).thenReturn(mockPurity);
      when(updateGoldPriceService.calculateGoldPrice(any(), any(), eq(unit)))
          .thenReturn(new BigDecimal("500.00"));

      assertThatCode(() -> addGoldTypeService.addGoldType(request)).doesNotThrowAnyException();
    }
  }
}
