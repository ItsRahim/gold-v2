package com.rahim.pricingservice.repository;

import com.rahim.pricingservice.entity.GoldPurity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @created 03/05/2025
 * @author Rahim Ahmed
 */
@Repository
public interface GoldPurityRepository extends JpaRepository<GoldPurity, Long> {
  Optional<GoldPurity> getGoldPuritiesByLabel(String label);
}
