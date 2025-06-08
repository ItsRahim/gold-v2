package com.rahim.authenticationservice.entity;

import com.rahim.authenticationservice.enums.BlacklistReason;
import com.rahim.authenticationservice.enums.TokenType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @ColumnDefault("gen_random_uuid()")
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotNull
  @Column(name = "token_hash", nullable = false)
  private String tokenHash;

  @NotNull
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @NotNull
  @Column(name = "expires_at", nullable = false)
  private OffsetDateTime expiresAt;

  @NotNull
  @Column(name = "blacklisted_at", nullable = false)
  private OffsetDateTime blacklistedAt;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "reason", nullable = false, length = 50)
  private BlacklistReason reason;
}
