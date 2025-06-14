package com.rahim.pricingservice.repository;

import com.rahim.pricingservice.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @created 14/06/2025
 * @author Rahim Ahmed
 */
@Repository
public interface GoldPriceHistoryRepository extends JpaRepository<PriceHistory, Integer> {}
