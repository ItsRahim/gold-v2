package com.rahim.pricingservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "AddGoldTypeRequest",
    description = "Request object for adding a new gold type",
    // language=json
    example =
        """
                    {
                        "name": "Sovereign - Victoria, Old Veiled Head",
                        "weight": 7.98,
                        "carat": "22K",
                        "description": "Gold Sovereign"
                    }
                """)
public class AddGoldTypeRequest {
  @Schema(
      description = "Name of the gold type",
      example = "24K Pure Gold",
      minLength = 1,
      maxLength = 100,
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Gold type name cannot be blank")
  @Size(min = 1, max = 100, message = "Gold type name must be between 1 and 100 characters")
  @JsonProperty("name")
  private String name;

  @Schema(
      description = "Purity of the gold type",
      example = "24K",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Gold purity cannot be blank")
  @JsonProperty("carat")
  private String carat;

  @Schema(
      description = "Weight of the gold type",
      example = "10.50",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("weight")
  @NotNull(message = "Weight cannot be null")
  @Positive(message = "Weight must be a positive number")
  private BigDecimal weight;

  @Schema(
      description = "Description of the gold type",
      example = "High-quality gold with unique characteristics",
      maxLength = 500)
  @JsonProperty("description")
  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;
}
