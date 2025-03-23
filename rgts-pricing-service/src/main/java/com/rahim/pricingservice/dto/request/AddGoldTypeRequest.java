package com.rahim.pricingservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Getter
@Setter
@AllArgsConstructor
public class AddGoldTypeRequest {
    private String name;
    private String carat;
    private BigDecimal weight;
    private String description;
}
