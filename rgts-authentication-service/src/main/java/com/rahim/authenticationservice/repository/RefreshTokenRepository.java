package com.rahim.authenticationservice.repository;

import com.rahim.authenticationservice.entity.RefreshToken;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
  /** Find refresh token by token string and user ID */
  Optional<RefreshToken> findByTokenAndUserId(String token, Integer userId);

  /** Find refresh token by token string */
  Optional<RefreshToken> findByToken(String token);

  /** Find all active (non-revoked, non-expired) tokens for a user */
  @Query(
      "SELECT rt FROM RefreshToken rt WHERE rt.userId = :userId AND rt.revoked = false AND rt.expiresAt > :now")
  List<RefreshToken> findActiveTokensByUserId(
      @Param("userId") Long userId, @Param("now") Instant now);

  /** Find all tokens for a user (active and inactive) */
  List<RefreshToken> findByUserId(Long userId);

  /** Check if a token exists and is valid */
  @Query(
      "SELECT COUNT(rt) > 0 FROM RefreshToken rt WHERE rt.token = :token AND rt.revoked = false AND rt.expiresAt > :now")
  boolean existsByTokenAndValid(@Param("token") String token, @Param("now") Instant now);

  /** Revoke all active tokens for a user */
  @Modifying
  @Query(
      "UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :now WHERE rt.userId = :userId AND rt.revoked = false")
  void revokeAllTokensForUser(@Param("userId") Integer userId, @Param("now") Instant now);

  /** Revoke all active tokens for a user (convenience method) */
  default void revokeAllTokensForUser(Integer userId) {
    revokeAllTokensForUser(userId, Instant.now());
  }

  /** Revoke a specific token */
  @Modifying
  @Query(
      "UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :now WHERE rt.token = :token")
  int revokeToken(@Param("token") String token, @Param("now") Instant now);

  /** Delete expired tokens */
  @Modifying
  @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
  int deleteExpiredTokens(@Param("now") Instant now);

  /** Delete revoked tokens older than specified date */
  @Modifying
  @Query("DELETE FROM RefreshToken rt WHERE rt.revoked = true AND rt.revokedAt < :before")
  int deleteRevokedTokensOlderThan(@Param("before") Instant before);

  @Modifying
  @Query("DELETE FROM RefreshToken rt WHERE rt.userId = :userId")
  int purgeTokenForUser(@Param("userId") int userId);

  /** Count active tokens for a user */
  @Query(
      "SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.userId = :userId AND rt.revoked = false AND rt.expiresAt > :now")
  long countActiveTokensByUserId(@Param("userId") Long userId, @Param("now") Instant now);

  /** Find tokens that will expire soon (for proactive refresh) */
  @Query(
      "SELECT rt FROM RefreshToken rt WHERE rt.revoked = false AND rt.expiresAt BETWEEN :now AND :expiringBefore")
  List<RefreshToken> findTokensExpiringSoon(
      @Param("now") Instant now, @Param("expiringBefore") Instant expiringBefore);
}
