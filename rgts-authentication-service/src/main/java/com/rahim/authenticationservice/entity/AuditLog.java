package com.rahim.authenticationservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.net.InetAddress;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    name = "audit_log",
    schema = "authentication-service",
    indexes = {
      @Index(name = "idx_audit_log_user_id", columnList = "user_id"),
      @Index(name = "idx_audit_log_created_at", columnList = "created_at")
    })
public class AuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @ColumnDefault("gen_random_uuid()")
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.SET_NULL)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @NotNull
  @Column(name = "action", nullable = false, length = 100)
  private String action;

  @NotNull
  @Column(name = "resource", nullable = false, length = 100)
  private String resource;

  @Column(name = "ip_address")
  private InetAddress ipAddress;

  @NotNull
  @Column(name = "success", nullable = false)
  private Boolean success;

  @Column(name = "details", columnDefinition = "jsonb")
  private String details;

  @NotNull
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime created_at;
}
