package com.rahim.authenticationservice.entity;

import com.rahim.authenticationservice.enums.LoginFailureReason;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.net.InetAddress;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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
    name = "login_attempts",
    schema = "authentication-service",
    indexes = {
      @Index(name = "idx_login_attempts_identifier", columnList = "identifier"),
      @Index(name = "idx_login_attempts_ip_address", columnList = "ip_address"),
      @Index(name = "idx_login_attempts_attempted_at", columnList = "attempted_at")
    })
public class LoginAttempt {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @ColumnDefault("gen_random_uuid()")
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotNull
  @Column(name = "identifier", nullable = false)
  private String identifier;

  @Column(name = "ip_address")
  private InetAddress ipAddress;

  @NotNull
  @ColumnDefault("false")
  @Column(name = "success", nullable = false)
  private boolean success;

  @Enumerated(EnumType.STRING)
  @Column(name = "failure_reason")
  private LoginFailureReason failureReason;

  @Column(name = "user_agent")
  private String userAgent;

  @NotNull
  @Column(name = "attempted_at", nullable = false)
  private OffsetDateTime attemptedAt;
}
