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
    name = "audit_logs",
    indexes = {
      @Index(name = "idx_audit_logs_user_id", columnList = "user_id"),
      @Index(name = "idx_audit_logs_timestamp", columnList = "timestamp")
    })
public class AuditLog {

  @Id @GeneratedValue private UUID id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @NotNull
  @Column(nullable = false, length = 100)
  private String action;

  @NotNull
  @Column(nullable = false, length = 100)
  private String resource;

  @NotNull
  @Column(nullable = false, length = 20)
  private String severity = "INFO";

  @Column(name = "ip_address")
  private InetAddress ipAddress;

  @Lob
  @Column(name = "user_agent")
  private String userAgent;

  @Column(columnDefinition = "jsonb")
  private String details;

  @NotNull
  @Column(nullable = false)
  private OffsetDateTime timestamp = OffsetDateTime.now();
}
