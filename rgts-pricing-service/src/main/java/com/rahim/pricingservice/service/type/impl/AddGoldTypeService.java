package com.rahim.pricingservice.service.type.impl;

import com.rahim.common.exception.BadRequestException;
import com.rahim.common.exception.DuplicateEntityException;
import com.rahim.common.exception.ServiceException;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.exception.GoldPriceCalculationException;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import com.rahim.pricingservice.service.price.IUpdateGoldPriceService;
import com.rahim.pricingservice.service.purity.IGoldPurityQueryService;
import com.rahim.pricingservice.service.type.IAddGoldTypeService;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = {BadRequestException.class, ServiceException.class})
public class AddGoldTypeService implements IAddGoldTypeService {
  private final IGoldPurityQueryService goldPurityQueryService;
  private final IUpdateGoldPriceService updateGoldPriceService;
  private final GoldTypeRepository goldTypeRepository;

  private static final String CARAT_REGEX = "^([1-9]|1[0-9]|2[0-4])[Kk]?$";
  private static final Pattern pattern = Pattern.compile(CARAT_REGEX);

  @Override
  public void addGoldType(AddGoldTypeRequest request) {
    if (request == null) {
      log.error("Received null AddGoldTypeRequest");
      throw new BadRequestException("Request body cannot be null");
    }

    validateAddGoldTypeRequest(request);

    try {
      GoldPurity purity = goldPurityQueryService.getGoldPurityByCaratLabel(request.getPurity());
      WeightUnit unit = WeightUnit.fromValue(request.getUnit());

      BigDecimal price =
          updateGoldPriceService.calculateGoldPrice(purity, request.getWeight(), unit);

      GoldType goldType =
          GoldType.builder()
              .name(request.getName())
              .purity(purity)
              .weight(request.getWeight())
              .unit(unit)
              .price(price)
              .description(request.getDescription())
              .build();

      goldTypeRepository.save(goldType);

      log.info(
          "Added new gold type: '{}', Carat: '{}', Price: {}",
          request.getName(),
          purity.getLabel(),
          price);
    } catch (GoldPriceCalculationException e) {
      log.error(
          "Failed to calculate gold price for new gold type '{}': {}",
          request.getName(),
          e.getMessage(),
          e);
      throw new BadRequestException("Could not add gold type: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("Invalid weight unit provided: {}", e.getMessage());
      throw new BadRequestException("Invalid weight unit: " + request.getUnit());
    } catch (Exception e) {
      log.error(
          "Unexpected error while adding gold type '{}': {}", request.getName(), e.getMessage());
      throw new ServiceException("Unexpected error occurred while adding gold type");
    }
  }

  private void validateAddGoldTypeRequest(AddGoldTypeRequest request) {
    if (request.getName() == null || request.getName().isEmpty()) {
      throw new BadRequestException("Gold type name is required");
    }

    if (goldTypeRepository.existsGoldTypeByNameIgnoreCase(request.getName())) {
      throw new DuplicateEntityException("Gold type already exists: " + request.getName());
    }

    if (!isValidGoldCarat(request.getPurity())) {
      throw new BadRequestException("Invalid carat label: " + request.getPurity());
    }

    if (request.getWeight() == null || request.getWeight().compareTo(BigDecimal.ZERO) <= 0) {
      throw new BadRequestException("Gold weight must be positive");
    }

    String unit = request.getUnit();
    if (StringUtils.isBlank(unit)) {
      throw new BadRequestException("Weight unit is required");
    }

    String description = request.getDescription();
    if (StringUtils.isBlank(description)) {
      throw new BadRequestException("Description is required");
    }
  }

  private boolean isValidGoldCarat(String input) {
    if (input == null) {
      return false;
    }

    Matcher matcher = pattern.matcher(input);
    return matcher.matches();
  }
}
