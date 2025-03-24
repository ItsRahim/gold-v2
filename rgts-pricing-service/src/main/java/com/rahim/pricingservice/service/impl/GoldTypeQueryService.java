package com.rahim.pricingservice.service.impl;

import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import com.rahim.pricingservice.service.IGoldTypeQueryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
@Service
@RequiredArgsConstructor
public class GoldTypeQueryService implements IGoldTypeQueryService {
    private static final Logger logger = LoggerFactory.getLogger(GoldTypeQueryService.class);
    private final GoldTypeRepository goldTypeRepository;

    @Override
    public List<GoldType> getAllGoldTypes() {
        return goldTypeRepository.findAll();
    }
}
