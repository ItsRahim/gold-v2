package com.rahim.pricingservice.service.type.impl;

import com.rahim.common.exception.BadRequestException;
import com.rahim.common.exception.EntityNotFoundException;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.exception.DuplicateGoldTypeException;
import com.rahim.pricingservice.exception.InvalidCaratException;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import com.rahim.pricingservice.service.purity.IGoldPurityQueryService;
import com.rahim.pricingservice.service.type.IAddGoldTypeService;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  private final IGoldPurityQueryService goldPurityQueryService;

  private static final String CARAT_REGEX = "^([1-9]|1[0-9]|2[0-4])[Kk]?$";
  private static final Pattern pattern = Pattern.compile(CARAT_REGEX);

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

    if (goldTypeRepository.existsGoldTypeByNameIgnoreCase(name)) {
      log.error("Gold type with name {}}' already exists", name);
      throw new DuplicateGoldTypeException("Gold type with name '" + name + "' already exists");
    }

    String caratLabel = request.getCaratLabel();
    if (!isValidGoldCarat(caratLabel)) {
      log.error("Invalid gold carat provided: '{}'.", caratLabel);
      throw new InvalidCaratException("Gold carat '" + caratLabel + "' is invalid");
    }

    GoldPurity goldPurity;
    try {
      goldPurity = goldPurityQueryService.getGoldPurityByCaratLabel(caratLabel);
    } catch (EntityNotFoundException e) {
      log.error("Gold purity not found", e);
      throw new BadRequestException("Gold purity not found");
    }

    BigDecimal weight = request.getWeight();
    if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
      log.error("Invalid weight provided: '{}'. Weight must be non-negative.", weight);
      throw new BadRequestException("Gold weight must be non-negative and greater than 0");
    }

    String unitValue = request.getUnit();
    if (unitValue == null || unitValue.isEmpty()) {
      log.error("Weight unit is null or empty");
      throw new BadRequestException("Weight unit is required");
    }

    WeightUnit unit;
    try {
      unit = WeightUnit.fromValue(unitValue);
    } catch (IllegalArgumentException e) {
      log.error("Invalid weight unit provided: '{}'", unitValue);
      throw new BadRequestException("Invalid weight unit: " + unitValue);
    }

    String description = request.getDescription();
    if (description == null || description.isEmpty()) {
      log.error("Empty or null description provided.");
      throw new BadRequestException("Gold description is required");
    }

    GoldType goldType =
        GoldType.builder()
            .name(name)
            .carat(goldPurity)
            .weight(weight)
            .unit(unit)
            .price(BigDecimal.ZERO)
            .description(description)
            .build();

    log.info(
        "Successfully added gold type: Name='{}', Carat='{}', Weight='{}', Unit='{}'",
        name,
        caratLabel,
        weight,
        unit.getValue());
    return goldTypeRepository.save(goldType);
  }

  private boolean isValidGoldCarat(String input) {
    if (input == null) {
      return false;
    }
    Matcher matcher = pattern.matcher(input);
    return matcher.matches();
  }
}
