package com.rahim.pricingservice.service.impl;

import com.rahim.common.exception.BadRequestException;
import com.rahim.common.exception.DuplicateEntityException;
import com.rahim.common.exception.EntityNotFoundException;
import com.rahim.common.exception.ServiceException;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.exception.GoldPriceCalculationException;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import com.rahim.pricingservice.service.IGoldTypeService;
import com.rahim.pricingservice.service.IQueryGoldPurityService;
import com.rahim.pricingservice.service.IQueryGoldTypeService;
import com.rahim.pricingservice.service.IUpdateGoldPriceService;
import com.rahim.storageservice.exception.MinioStorageException;
import com.rahim.storageservice.exception.StorageException;
import com.rahim.storageservice.service.StorageService;
import com.rahim.storageservice.service.StorageServiceFactory;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Slf4j
@Service
@Transactional(rollbackFor = {BadRequestException.class, ServiceException.class})
public class GoldTypeService implements IGoldTypeService {
  private final IQueryGoldTypeService queryGoldTypeService;
  private final IQueryGoldPurityService goldPurityQueryService;
  private final IUpdateGoldPriceService updateGoldPriceService;
  private final GoldTypeRepository goldTypeRepository;
  private final StorageService storageService;

  public GoldTypeService(
      IQueryGoldTypeService queryGoldTypeService,
      IQueryGoldPurityService goldPurityQueryService,
      IUpdateGoldPriceService updateGoldPriceService,
      GoldTypeRepository goldTypeRepository,
      StorageServiceFactory factory) {
    this.queryGoldTypeService = queryGoldTypeService;
    this.goldPurityQueryService = goldPurityQueryService;
    this.updateGoldPriceService = updateGoldPriceService;
    this.goldTypeRepository = goldTypeRepository;
    this.storageService = factory.getStorageService();
  }

  private static final String BUCKET_NAME = "gold-types";
  private static final String CARAT_REGEX = "^([1-9]|1[0-9]|2[0-4])[Kk]?$";
  private static final Pattern pattern = Pattern.compile(CARAT_REGEX);

  @Override
  public void addGoldType(AddGoldTypeRequest request, MultipartFile file) {
    if (request == null) {
      log.error("Received null AddGoldTypeRequest");
      throw new BadRequestException("Request body cannot be null");
    }

    validateAddGoldTypeRequest(request, file);

    try {
      log.info("Starting gold type creation process for: {}", request.getName());

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

      log.debug("Saving gold type entity to database");
      GoldType savedGoldType = goldTypeRepository.save(goldType);

      log.info(
          "Uploading image for gold type: {} to bucket: {}", savedGoldType.getName(), BUCKET_NAME);
      String imageUrl =
          storageService.uploadImage(BUCKET_NAME, savedGoldType.getId().toString(), file);

      savedGoldType.setImageUrl(imageUrl);
      goldTypeRepository.save(savedGoldType);

      log.info(
          "Successfully added new gold type: '{}', Carat: '{}', Price: {}, Image URL: {}",
          request.getName(),
          purity.getLabel(),
          price,
          imageUrl);

    } catch (GoldPriceCalculationException e) {
      log.error(
          "Failed to calculate gold price for new gold type '{}': {}",
          request.getName(),
          e.getMessage(),
          e);
      throw new BadRequestException("Could not add gold type due to price calculation error");
    } catch (StorageException e) {
      log.error(
          "Storage operation failed while adding gold type '{}': {}",
          request.getName(),
          e.getMessage());
      throw new ServiceException("Failed to upload image for gold type");
    } catch (MinioStorageException e) {
      log.error(
          "MinIO storage operation failed while adding gold type '{}': {}",
          request.getName(),
          e.getMessage());
      throw new ServiceException("Failed to upload image to storage");
    } catch (IllegalArgumentException e) {
      log.error(
          "Invalid weight unit provided for gold type '{}': {}", request.getName(), e.getMessage());
      throw new BadRequestException("Invalid weight unit: " + request.getUnit());
    } catch (Exception e) {
      log.error(
          "Unexpected error while adding gold type '{}': {}", request.getName(), e.getMessage(), e);
      throw new ServiceException("Unexpected error occurred while adding gold type");
    }
  }

  @Override
  public void deleteGoldTypeById(UUID id) {
    log.info("Attempting to delete gold type with ID: {}", id);

    try {
      GoldType goldType = queryGoldTypeService.getGoldTypeById(id);

      if (goldType.getImageUrl() != null && !goldType.getImageUrl().trim().isEmpty()) {
        log.info("Deleting image from storage for gold type: {}", goldType.getName());
        storageService.deleteObject(BUCKET_NAME, goldType.getImageUrl());
        log.info("Successfully deleted image from storage for gold type: {}", goldType.getName());
      }

      goldTypeRepository.delete(goldType);
      log.info("Successfully deleted gold type: {} (ID: {})", goldType.getName(), id);

    } catch (EntityNotFoundException e) {
      throw e;
    } catch (StorageException | MinioStorageException e) {
      log.warn(
          "Failed to delete image from storage for gold type with ID '{}': {}. Proceeding with entity deletion.",
          id,
          e.getMessage());
      throw new ServiceException("Failed to delete image from storage for gold type");
    } catch (Exception e) {
      log.error(
          "Unexpected error while deleting gold type with ID '{}': {}", id, e.getMessage(), e);
      throw new ServiceException("Unexpected error occurred while deleting gold type");
    }
  }

  private void validateAddGoldTypeRequest(AddGoldTypeRequest request, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BadRequestException("No image file provided");
    }

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
