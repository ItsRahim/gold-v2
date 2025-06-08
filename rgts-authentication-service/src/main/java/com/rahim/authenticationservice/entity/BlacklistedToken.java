package com.rahim.authenticationservice.entity;

import com.rahim.authenticationservice.enums.BlacklistReason;
import com.rahim.authenticationservice.enums.TokenType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    name = "blacklisted_tokens",
    indexes = {
      @Index(name = "idx_blacklisted_tokens_user_id", columnList = "user_id"),
      @Index(name = "idx_blacklisted_tokens_expires_at", columnList = "expires_at")
    })
public class BlacklistedToken {

  @Id @GeneratedValue private UUID id;

  @NotNull
  @Column(nullable = false, unique = true)
  private UUID jti;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "token_type", nullable = false, length = 20)
  private TokenType tokenType;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "reason", nullable = false, length = 50)
  private BlacklistReason reason;

  @NotNull
  @Column(name = "blacklisted_at", nullable = false)
  private OffsetDateTime blacklistedAt = OffsetDateTime.now();

  @NotNull
  @Column(name = "expires_at", nullable = false)
  private OffsetDateTime expiresAt;

  @Column(name = "invalidated_by_user", nullable = false)
  private boolean invalidatedByUser = false;
}
