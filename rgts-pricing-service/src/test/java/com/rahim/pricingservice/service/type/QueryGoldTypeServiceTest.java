package com.rahim.pricingservice.service.type;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.rahim.common.response.AbstractResponseDTO;
import com.rahim.pricingservice.BaseUnitTest;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.exception.GoldTypeNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

/**
 * @created 05/04/2025
 * @author Rahim Ahmed
 */
public class QueryGoldTypeServiceTest extends BaseUnitTest {
  @Autowired private IQueryGoldTypeService queryGoldTypeService;
  @Autowired private IAddGoldTypeService addGoldTypeService;

  private void addGoldType() {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("Gold Coin", "22K", BigDecimal.TEN, "g", "Valid");
    addGoldTypeService.addGoldType(request);
  }

  @Test
  public void getAllGoldTypes_returnsPagedResult() {
    addGoldType();
    Page<AbstractResponseDTO> result = queryGoldTypeService.getAllGoldTypes(0, 10);

    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(1);
  }

  @Test
  void getGoldTypeById_existingId_returnsGoldType() {
    addGoldType();
    GoldType result = queryGoldTypeService.getGoldTypeById(1);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Gold Coin");
  }

  @Test
  void getGoldTypeById_nonExistingId_throwsException() {
    assertThatThrownBy(() -> queryGoldTypeService.getGoldTypeById(99))
        .isInstanceOf(GoldTypeNotFoundException.class)
        .hasMessage("Gold Type with ID: 99 not found");
  }
}
