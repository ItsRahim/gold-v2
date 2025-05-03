package com.rahim.pricingservice.dto.response;

import com.rahim.pricingservice.enums.WeightUnit;
import lombok.*;

import java.math.BigDecimal;

/**
 * @created 03/05/2025
 * @author Rahim Ahmed
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldTypeResponse {
  private Integer id;
  private String name;
  private String carat;
  private BigDecimal weight;
  private WeightUnit unit;
  private String description;
  private BigDecimal price;
}
