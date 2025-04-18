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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AddGoldTypeService implements IAddGoldTypeService {
  private final GoldTypeRepository goldTypeRepository;
  private final GoldCaratUtil goldCaratUtil;

  @Override
  public GoldType addGoldType(AddGoldTypeRequest request) {
    if (request == null) {
      log.error("AddGoldTypeRequest request body is null");
      throw new BadRequestException("Request body is null");
    }

    String name = request.getName();
    if (name == null || name.isEmpty()) {
      log.error("Gold type name is null or empty");
      throw new BadRequestException("Gold type name is null or empty");
    }

    if (goldTypeRepository.existsGoldTypeByName(name)) {
      log.error("Gold type with name {}}' already exists", name);
      throw new DuplicateEntityException("Gold type with name '" + name + "' already exists");
    }

    String carat = request.getCarat();
    if (!goldCaratUtil.isValidGoldCarat(carat)) {
      log.error("Invalid gold carat provided: '{}'.", carat);
      throw new InvalidCaratException("Gold carat '" + carat + "' is invalid");
    }

    BigDecimal weight = request.getWeight();
    if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
      log.error("Invalid weight provided: '{}'. Weight must be non-negative.", weight);
      throw new BadRequestException("Gold weight must be non-negative and greater than 0");
    }

    String description = request.getDescription();
    if (description == null || description.isEmpty()) {
      log.error("Empty or null description provided.");
      throw new BadRequestException("Gold description is required");
    }

    GoldType goldType =
        GoldType.builder().name(name).carat(carat).weight(weight).description(description).build();

    log.info(
        "Successfully added gold type: Name='{}', Carat='{}', Weight='{}'", name, carat, weight);
    return goldTypeRepository.save(goldType);
  }
}
