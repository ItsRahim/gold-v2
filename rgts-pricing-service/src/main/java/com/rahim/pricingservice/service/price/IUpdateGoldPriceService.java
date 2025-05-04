package com.rahim.pricingservice.service.price;

import com.rahim.pricingservice.dto.payload.GoldPriceUpdateDTO;

/**
 * @created 03/05/2025
 * @author Rahim Ahmed
 */
public interface IUpdateGoldPriceService {
    void updateBasePrice(GoldPriceUpdateDTO goldPriceUpdateDTO);
}
