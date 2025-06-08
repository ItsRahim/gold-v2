package com.rahim.authenticationservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "verification_codes",
    schema = "authentication-service",
    indexes = {
      @Index(name = "idx_verification_codes_user_type", columnList = "user_id, type"),
      @Index(name = "idx_verification_codes_code", columnList = "code"),
      @Index(name = "idx_verification_codes_expires_at", columnList = "expires_at")
    })
public class VerificationCode {
  @Id
  @ColumnDefault("gen_random_uuid()")
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Size(max = 6)
  @NotNull
  @Column(name = "code", nullable = false, length = 6)
  private String code;

  @Size(max = 20)
  @NotNull
  @Column(name = "type", nullable = false, length = 20)
  private String type;

  @NotNull
  @ColumnDefault("now()")
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @NotNull
  @Column(name = "expires_at", nullable = false)
  private OffsetDateTime expiresAt;

  @ColumnDefault("0")
  @Column(name = "attempts")
  private Integer attempts;
}
