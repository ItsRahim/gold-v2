package com.rahim.pricingservice.service.impl;

import com.rahim.common.exception.DuplicateEntityException;
import com.rahim.common.exception.base.BadRequestException;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.exception.InvalidCaratException;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import com.rahim.pricingservice.service.IAddGoldTypeService;
import com.rahim.pricingservice.util.GoldCaratUtil;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Service
@RequiredArgsConstructor
public class AddGoldTypeService implements IAddGoldTypeService {
  private static final Logger logger = LoggerFactory.getLogger(AddGoldTypeService.class);
  private final GoldTypeRepository goldTypeRepository;
  private final GoldCaratUtil goldCaratUtil;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addGoldType(AddGoldTypeRequest request) {
    if (request == null) {
      logger.error("AddGoldTypeRequest request body is null");
      throw new BadRequestException("Request body is null");
    }

    String name = request.getName();
    if (name == null || name.isEmpty()) {
      logger.error("Gold type name is null or empty");
      throw new BadRequestException("Gold type name is null or empty");
    }

    if (goldTypeRepository.existsGoldTypeByName(name)) {
      logger.error("Gold type with name {}}' already exists", name);
      throw new DuplicateEntityException("Gold type with name '" + name + "' already exists");
    }

    String carat = request.getCarat();
    if (!goldCaratUtil.isValidGoldCarat(carat)) {
      logger.error("Invalid gold carat provided: '{}'.", carat);
      throw new InvalidCaratException("Gold carat '" + carat + "' is invalid");
    }

    BigDecimal weight = request.getWeight();
    if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
      logger.error("Invalid weight provided: '{}'. Weight must be non-negative.", weight);
      throw new BadRequestException("Gold weight must be non-negative and greater than 0");
    }

    String description = request.getDescription();
    if (description == null || description.isEmpty()) {
      logger.error("Empty or null description provided.");
      throw new BadRequestException("Gold description is required");
    }

    GoldType goldType =
        GoldType.builder().name(name).carat(carat).weight(weight).description(description).build();

    goldTypeRepository.save(goldType);
    logger.info(
        "Successfully added gold type: Name='{}', Carat='{}', Weight='{}'", name, carat, weight);
  }
}
