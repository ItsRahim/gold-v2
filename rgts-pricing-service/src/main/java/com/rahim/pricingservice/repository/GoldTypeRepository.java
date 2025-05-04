package com.rahim.pricingservice.repository;

import com.rahim.pricingservice.entity.GoldType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Repository
public interface GoldTypeRepository extends JpaRepository<GoldType, Long> {
  boolean existsGoldTypeByNameIgnoreCase(String name);
}
