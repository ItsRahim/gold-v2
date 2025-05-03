package com.rahim.pricingservice.service.impl;

import com.rahim.pricingservice.dto.grpc.GoldPriceUpdateDTO;
import com.rahim.pricingservice.entity.GoldPrice;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.repository.GoldPriceRepository;
import com.rahim.pricingservice.service.IUpdateGoldPriceService;
import com.rahim.pricingservice.util.GoldCaratUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;
import java.math.BigDecimal;
import java.util.List;

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
  private final GoldCaratUtil goldCaratUtil;

  @Override
  public void updateGoldPrice(GoldPriceUpdateDTO goldPriceUpdateDTO) {
    BigDecimal currentPrice = goldPriceUpdateDTO.getPrice();

    List<GoldPrice> allGoldPrices = goldPriceRepository.findAll();
    for (GoldPrice goldPrice : allGoldPrices) {
      GoldType goldType = goldPrice.getGoldType();
      String carat = goldType.getCarat();
      BigDecimal purity = goldCaratUtil.getPurity(carat);
      BigDecimal weight = goldType.getWeight();
    }
  }
}
