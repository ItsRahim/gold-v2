package com.rahim.pricingservice.service;

import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Service
@RequiredArgsConstructor
public class GoldTypeService implements IGoldTypeService {
    private static final Logger logger = LoggerFactory.getLogger(GoldTypeService.class);

    @Override
    public void addGoldType(AddGoldTypeRequest request) {

    }
}
