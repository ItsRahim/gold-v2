package com.rahim.pricingservice.service.impl;

import com.rahim.pricingservice.repository.GoldTypeRepository;
import com.rahim.pricingservice.service.IQueryGoldTypeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
@Service
@RequiredArgsConstructor
public class QueryGoldTypeService implements IQueryGoldTypeService {
    private static final Logger logger = LoggerFactory.getLogger(QueryGoldTypeService.class);
    private final GoldTypeRepository goldTypeRepository;
}
