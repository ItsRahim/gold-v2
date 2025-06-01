package com.rahim.authenticationservice.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "refresh_tokens",
    schema = "authentication-service",
    indexes = {
      @Index(name = "idx_refresh_token", columnList = "token"),
      @Index(name = "idx_user_id", columnList = "user_id"),
      @Index(name = "idx_expires_at", columnList = "expires_at")
    })
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "token", nullable = false, unique = true, length = 500)
  private String token;

  @Column(name = "user_id", nullable = false)
  private Integer userId;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "revoked", nullable = false)
  private boolean revoked;

  @Column(name = "revoked_at")
  private Instant revokedAt;

  @Column(name = "ip_address", length = 45)
  private String ipAddress;

  public boolean isExpired() {
    return Instant.now().isAfter(expiresAt);
  }

  public boolean isValid() {
    return !isExpired() && !revoked;
  }
}
