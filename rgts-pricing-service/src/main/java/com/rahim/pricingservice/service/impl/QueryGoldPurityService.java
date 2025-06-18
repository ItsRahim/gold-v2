package com.rahim.pricingservice.service.impl;

import com.rahim.common.exception.EntityNotFoundException;
import com.rahim.pricingservice.entity.GoldPurity;
import com.rahim.pricingservice.repository.GoldPurityRepository;
import com.rahim.pricingservice.service.IQueryGoldPurityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @created 03/05/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryGoldPurityService implements IQueryGoldPurityService {
  private final GoldPurityRepository goldPurityRepository;

  @Override
  public GoldPurity getGoldPurityByCaratLabel(String label) {
    return goldPurityRepository
        .getGoldPuritiesByLabel(label)
        .orElseThrow(
            () -> new EntityNotFoundException("Gold purity not found for label: " + label));
  }

  @Override
  public List<GoldPurity> getAllGoldPurities() {
    return goldPurityRepository.findAll();
  }
}
