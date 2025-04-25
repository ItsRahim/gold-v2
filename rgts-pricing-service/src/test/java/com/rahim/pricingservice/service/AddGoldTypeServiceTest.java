package com.rahim.pricingservice.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import com.rahim.common.exception.base.BadRequestException;
import com.rahim.pricingservice.BaseUnitTest;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
public class AddGoldTypeServiceTest extends BaseUnitTest {

  @Autowired private IAddGoldTypeService addGoldTypeService;

  @Autowired private GoldTypeRepository goldTypeRepository;

  @Test
  public void shouldAddGoldType() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "name", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    addGoldTypeService.addGoldType(request);
    assertThat(goldTypeRepository.existsGoldTypeByName("name")).isTrue();
  }

  @Test
  public void shouldThrowExceptionWhenAddGoldTypeRequestIsNull() {
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Request body is null");
  }

  @Test
  public void shouldThrowExceptionWhenAddGoldTypeRequestNameIsEmpty() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold type name is null or empty");
  }

  @Test
  public void shouldThrowExceptionWhenAddGoldTypeRequestNameIsNull() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            null, "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold type name is null or empty");
  }

  @Test
  public void shouldThrowExceptionWhenAddGoldTypeRequestNameIsExists() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "name", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    addGoldTypeService.addGoldType(request);

    AddGoldTypeRequest request2 =
        new AddGoldTypeRequest(
            "name", "19K", BigDecimal.ONE, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request2))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold type with name '" + request2.getName() + "' already exists");

    AddGoldTypeRequest request3 =
        new AddGoldTypeRequest(
            "non-duplicate", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    assertThatCode(() -> addGoldTypeService.addGoldType(request3)).doesNotThrowAnyException();
    assertThat(goldTypeRepository.existsGoldTypeByName(request3.getName())).isTrue();
  }

  @Test
  public void shouldThrowExceptionWhenAddGoldTypeRequestCaratIsInvalid() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "name", "25K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold carat '" + request.getCarat() + "' is invalid");

    AddGoldTypeRequest request2 =
        new AddGoldTypeRequest(
            "name2", "2.5K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request2))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold carat '" + request2.getCarat() + "' is invalid");

    AddGoldTypeRequest request3 =
        new AddGoldTypeRequest(
            "name3", "-12K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request3))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold carat '" + request3.getCarat() + "' is invalid");

    AddGoldTypeRequest request4 =
        new AddGoldTypeRequest(
            "name3", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    assertThatCode(() -> addGoldTypeService.addGoldType(request4)).doesNotThrowAnyException();
  }

  @Test
  public void shouldThrowExceptionWhenAddGoldTypeRequestWeightIsInvalid() {
    AddGoldTypeRequest request1 =
        new AddGoldTypeRequest("name1", "22K", null, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request1))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold weight must be non-negative and greater than 0");

    AddGoldTypeRequest request2 =
        new AddGoldTypeRequest(
            "name2", "22K", new BigDecimal("-1"), WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request2))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold weight must be non-negative and greater than 0");

    AddGoldTypeRequest request3 =
        new AddGoldTypeRequest(
            "name3", "22K", BigDecimal.ZERO, WeightUnit.GRAM.getValue(), "description");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request3))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold weight must be non-negative and greater than 0");

    AddGoldTypeRequest request4 =
        new AddGoldTypeRequest(
            "name3", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "description");
    assertThatCode(() -> addGoldTypeService.addGoldType(request4)).doesNotThrowAnyException();
  }

  @Test
  public void shouldThrowExceptionWhenAddGoldTypeRequestDescriptionIsInvalid() {
    AddGoldTypeRequest request1 =
        new AddGoldTypeRequest("name1", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), null);
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request1))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold description is required");

    AddGoldTypeRequest request2 =
        new AddGoldTypeRequest("name2", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "");
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request2))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Gold description is required");

    AddGoldTypeRequest request3 =
        new AddGoldTypeRequest(
            "name3", "22K", BigDecimal.TEN, WeightUnit.GRAM.getValue(), "Valid description");
    assertThatCode(() -> addGoldTypeService.addGoldType(request3)).doesNotThrowAnyException();
  }

  @Test
  public void shouldThrowExceptionWhenAddGoldTypeRequestWeighUnitIsInvalid() {
    AddGoldTypeRequest request =
            new AddGoldTypeRequest("name1", "22K", BigDecimal.TEN, "ml", null);
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Invalid weight unit: " + request.getUnit());

    AddGoldTypeRequest request2 =
            new AddGoldTypeRequest("name1", "22K", BigDecimal.TEN, null, null);
    assertThatThrownBy(() -> addGoldTypeService.addGoldType(request2))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Weight unit is required");
  }
}
