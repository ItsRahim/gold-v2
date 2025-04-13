package com.rahim.pricingservice.service;

import com.rahim.pricingservice.BaseUnitTest;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.exception.GoldTypeNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 * @created 05/04/2025
 * @author Rahim Ahmed
 */
public class QueryGoldTypeServiceTest extends BaseUnitTest {
  @Autowired private IQueryGoldTypeService queryGoldTypeService;

  @Test
  public void getAllGoldTypes_returnsPagedResult() {
    Page<GoldType> result = queryGoldTypeService.getAllGoldTypes(0, 10);

    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(24);
    assertThat(result.getContent().get(0).getName()).isEqualTo("1 Carat");
  }

  @Test
  void getGoldType_existingId_returnsGoldType() {
    GoldType result = queryGoldTypeService.getGoldType(1L);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("24 Carat");
  }

  @Test
  void getGoldType_nonExistingId_throwsException() {
    assertThatThrownBy(() -> queryGoldTypeService.getGoldType(99L))
        .isInstanceOf(GoldTypeNotFoundException.class)
        .hasMessage("Gold Type with ID: 99 not found");
  }
}
