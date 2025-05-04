package com.rahim.pricingservice.service.purity;

import com.rahim.common.exception.EntityNotFoundException;
import com.rahim.pricingservice.BaseUnitTest;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.service.purity.impl.GoldPurityQueryService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 * @created 04/05/2025
 * @author Rahim Ahmed
 */
public class GoldPurityQueryServiceTest extends BaseUnitTest {

  @Autowired private GoldPurityQueryService goldPurityQueryService;

  @Test
  void shouldReturnGoldPurity_whenGoldPurityFound() {
    String caratLabel = "24K";
    GoldPurity result = goldPurityQueryService.getGoldPurityByCaratLabel(caratLabel);

    assertThat(result).isNotNull();
    assertThat("24K").isEqualTo(result.getLabel());
  }

  @Test
  void shouldThrowEntityNotFoundException_whenGoldPurityNotFound() {
    String caratLabel = "30K";

    assertThatThrownBy(() -> goldPurityQueryService.getGoldPurityByCaratLabel(caratLabel))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Gold purity not found for label: " + caratLabel);
  }
}
