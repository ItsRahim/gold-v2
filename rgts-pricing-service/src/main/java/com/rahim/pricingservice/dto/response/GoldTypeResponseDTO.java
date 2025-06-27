package com.rahim.pricingservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldTypeResponseDTO {
  private UUID id;
  private String name;
  private String purity;
  private String weight;
  private String description;
  private BigDecimal price;
  private String imageUrl;
}
