package com.rahim.pricingservice.service.impl;

import com.rahim.common.exception.InitialisationException;
import com.rahim.pricingservice.dto.grpc.GoldPriceUpdateDTO;
import com.rahim.pricingservice.service.IQueryGoldTypeService;
import com.rahim.pricingservice.service.IUpdateGoldPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @created 03/05/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UpdateGoldPriceService implements IUpdateGoldPriceService {
    private final IQueryGoldTypeService queryGoldTypeService;
    private final ConcurrentHashMap<String, BigDecimal> caratPrices = new ConcurrentHashMap<>();

    @PostConstruct
    public void initialiseCache() {
        log.info("Initializing gold price cache from database...");
        caratPrices.clear();
        try {

        } catch (Exception e) {
            log.error("Failed to initialise gold price cache from database: {}.", e.getMessage(), e);
            throw new InitialisationException("Unable to load gold prices at startup.", e);
        }
    }

    @Override
    public void updateGoldPrice(GoldPriceUpdateDTO goldPriceUpdateDTO) {

    }
}
