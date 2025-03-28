package com.rahim.pricingservice.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rahim Ahmed
 * @created 16/03/2025
 */
@RestController
@RequestMapping("/api/v2/pricing-service/price")
@RequiredArgsConstructor
public class GoldPriceController {
  private static final Logger logger = LoggerFactory.getLogger(GoldPriceController.class);
}
