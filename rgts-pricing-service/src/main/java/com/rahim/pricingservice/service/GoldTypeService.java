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
    private final GoldCaratUtil goldCaratUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGoldType(AddGoldTypeRequest request) {
        if (request == null) {
            logger.error("[addGoldType] Request body is null");
            throw new BadRequestException("Request body is null");
        }

        String name = request.getName();
        if (name == null || goldTypeRepository.existsGoldTypeByName(name)) {
            logger.error("[addGoldType] Gold type with name '{}' already exists or is null", name);
            throw new BadRequestException("Gold type with name '" + name + "' already exists");
        }

        String carat = request.getCarat();
        if (!goldCaratUtil.isValidGoldCarat(carat)) {
            logger.error("[addGoldType] Invalid gold carat provided: '{}'.", carat);
            throw new BadRequestException("Gold carat '" + carat + "' is invalid");
        }

        BigDecimal weight = request.getWeight();
        if (weight == null || weight.compareTo(BigDecimal.ZERO) < 0) {
            logger.error("[addGoldType] Invalid weight provided: '{}'. Weight must be non-negative.", weight);
            throw new BadRequestException("Gold weight must be non-negative");
        }

        String description = request.getDescription();
        if (description == null || description.isEmpty()) {
            logger.error("[addGoldType] Empty or null description provided.");
            throw new BadRequestException("Gold description is required");
        }

        GoldType goldType = GoldType.builder()
                .name(name)
                .carat(carat)
                .weight(weight)
                .description(description)
                .build();

        goldTypeRepository.save(goldType);
        logger.info("[addGoldType] Successfully added gold type: Name='{}', Carat='{}', Weight='{}'", name, carat, weight);
    }
}
