package com.rahim.authenticationservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.net.InetAddress;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "refresh_tokens",
    indexes = {
      @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"),
      @Index(name = "idx_refresh_tokens_expires_at", columnList = "expires_at")
    })
public class RefreshToken {

  @Id @GeneratedValue private UUID id;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @NotNull
  @Column(name = "token_hash", nullable = false, unique = true)
  private String tokenHash;

  @NotNull
  @Column(name = "expires_at", nullable = false)
  private OffsetDateTime expiresAt;

  @NotNull
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  @Column(name = "last_used_at")
  private OffsetDateTime lastUsedAt;

  @Column(name = "device_info")
  private String deviceInfo;

  @Column(name = "ip_address")
  private InetAddress ipAddress;
}
