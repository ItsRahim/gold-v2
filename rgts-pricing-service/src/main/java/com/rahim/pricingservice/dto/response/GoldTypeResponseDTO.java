package com.rahim.pricingservice.dto.response;

import com.rahim.common.response.AbstractResponseDTO;
import com.rahim.pricingservice.enums.WeightUnit;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldTypeResponseDTO extends AbstractResponseDTO {
  private Integer id;
  private String name;
  private String purity;
  private String weight;
  private String description;
  private BigDecimal price;
}
