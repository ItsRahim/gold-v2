package com.rahim.pricingservice.service;

import com.rahim.common.exception.BadRequestException;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import com.rahim.pricingservice.util.GoldCaratUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Service
@RequiredArgsConstructor
public class GoldTypeService implements IGoldTypeService {
    private static final Logger logger = LoggerFactory.getLogger(GoldTypeService.class);
    private final GoldTypeRepository goldTypeRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGoldType(AddGoldTypeRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is null");
        }

        String name = request.getName();
        if (name == null || goldTypeRepository.existsGoldTypeByName(name)) {
            logger.error("Some Error");
            throw new BadRequestException("Gold type with name '" + name + "' already exists");
        }

        String carat = request.getCarat();
        if (!GoldCaratUtil.isValidGoldCarat(carat)) {
            throw new BadRequestException("Gold carat '" + carat + "' is invalid");
        }

        BigDecimal weight = request.getWeight();
        if (weight == null || weight.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Gold weight is negative");
        }

        String description = request.getDescription();
        if (description == null || description.isEmpty()) {
            throw new BadRequestException("Gold description is empty");
        }

        GoldType goldType = GoldType.builder()
                .name(name)
                .carat(carat)
                .weight(weight)
                .description(description)
                .build();

        goldTypeRepository.save(goldType);
    }
}
