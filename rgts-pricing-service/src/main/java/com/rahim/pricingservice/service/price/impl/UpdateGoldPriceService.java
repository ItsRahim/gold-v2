package com.rahim.pricingservice.service.price.impl;

import com.rahim.pricingservice.dto.payload.GoldPriceUpdateDTO;
import com.rahim.pricingservice.repository.GoldPriceRepository;
import com.rahim.pricingservice.service.price.IUpdateGoldPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @created 03/05/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UpdateGoldPriceService implements IUpdateGoldPriceService {
  private final GoldPriceRepository goldPriceRepository;

  @Override
  public void updateGoldPrice(GoldPriceUpdateDTO goldPriceUpdateDTO) {
  }
}
