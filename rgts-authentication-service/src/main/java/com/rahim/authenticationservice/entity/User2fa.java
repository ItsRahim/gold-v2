package com.rahim.authenticationservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
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
    name = "user_2fa",
    schema = "authentication-service",
    indexes = {@Index(name = "idx_user_2fa_user_id", columnList = "user_id", unique = true)})
public class User2fa {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @ColumnDefault("gen_random_uuid()")
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotNull
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Size(max = 255)
  @NotNull
  @Column(name = "secret", nullable = false)
  private String secret;

  @ColumnDefault("false")
  @Column(name = "enabled")
  private Boolean enabled;

  @Column(name = "backup_codes")
  private List<String> backupCodes;

  @NotNull
  @ColumnDefault("now()")
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "enabled_at")
  private OffsetDateTime enabledAt;
}
