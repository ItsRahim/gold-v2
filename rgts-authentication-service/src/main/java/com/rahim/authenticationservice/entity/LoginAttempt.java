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
    name = "login_attempts",
    indexes = {
      @Index(name = "idx_login_attempts_user_id", columnList = "user_id"),
      @Index(name = "idx_login_attempts_attempted_at", columnList = "attempted_at")
    })
public class LoginAttempt {

  @Id @GeneratedValue private UUID id;

  @NotNull
  @Column(name = "email_or_username", nullable = false)
  private String emailOrUsername;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @NotNull
  @Column(nullable = false)
  private boolean success;

  @Column(name = "failure_reason")
  private String failureReason;

  @Column(name = "ip_address")
  private InetAddress ipAddress;

  @Column(name = "user_agent")
  private String userAgent;

  @Column(name = "geo_location")
  private String geoLocation;

  @NotNull
  @Column(name = "attempted_at", nullable = false)
  private OffsetDateTime attemptedAt = OffsetDateTime.now();
}
