package com.rahim.pricingservice.service.impl;

import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.exception.GoldTypeNotFoundException;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import com.rahim.pricingservice.service.IQueryGoldTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryGoldTypeService implements IQueryGoldTypeService {
  private final GoldTypeRepository goldTypeRepository;

  @Override
  public Page<GoldType> getAllGoldTypes(int page, int size) {
    return goldTypeRepository.findAll(PageRequest.of(page, size, Sort.by("name")));
  }

  @Override
  public GoldType getGoldType(long id) {
    return goldTypeRepository
        .findById(id)
        .orElseThrow(
            () -> new GoldTypeNotFoundException("Gold Type with ID: " + id + " not found"));
  }
}
