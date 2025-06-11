package com.rahim.authenticationservice.repository;

import com.rahim.authenticationservice.entity.VerificationCode;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.rahim.authenticationservice.enums.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, UUID> {

  @Query("SELECT vc FROM VerificationCode vc WHERE vc.user.id = :userId AND vc.type = :type")
  Optional<VerificationCode> findByUserIdAndType(
      @Param("userId") UUID userId, @Param("type") VerificationType type);

  @Query(
      "SELECT vc FROM VerificationCode vc WHERE vc.expiresAt < :now AND vc.createdAt < :oldestToKeep")
  List<VerificationCode> findCodesForCleanup(
      @Param("now") OffsetDateTime now, @Param("oldestToKeep") OffsetDateTime oldestToKeep);
}
