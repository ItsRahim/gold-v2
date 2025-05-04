package com.rahim.pricingservice.controller;

import com.rahim.pricingservice.constant.Endpoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rahim Ahmed
 * @created 16/03/2025
 */
@Slf4j
@RestController
@RequestMapping(Endpoints.GOLD_PRICE_ENDPOINT)
@RequiredArgsConstructor
public class GoldPriceController {}
