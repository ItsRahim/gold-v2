package com.rahim.pricingservice.repository;

import com.rahim.pricingservice.entity.GoldType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Repository
public interface GoldTypeRepository extends JpaRepository<GoldType, UUID> {
  boolean existsGoldTypeByNameIgnoreCase(String name);

  Optional<GoldType> findGoldTypeByNameIgnoreCase(String name);
}
