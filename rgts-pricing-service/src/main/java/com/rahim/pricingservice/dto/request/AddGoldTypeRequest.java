package com.rahim.pricingservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddGoldTypeRequest {
    @JsonProperty("name")
    private String name;

    @JsonProperty("carat")
    private String carat;

    @JsonProperty("weight")
    private BigDecimal weight;

    @JsonProperty("description")
    private String description;
}
