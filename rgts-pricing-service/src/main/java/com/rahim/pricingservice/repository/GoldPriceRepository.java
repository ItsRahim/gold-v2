package com.rahim.pricingservice.repository;

import com.rahim.pricingservice.entity.GoldPrice;
import com.rahim.pricingservice.entity.GoldPurity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @created 03/05/2025
 * @author Rahim Ahmed
 */
@Repository
public interface GoldPriceRepository extends JpaRepository<GoldPrice, Integer> {
  GoldPrice getGoldPriceByPurity(GoldPurity goldPurity);
}
