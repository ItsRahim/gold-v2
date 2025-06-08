package com.rahim.authenticationservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "users",
    schema = "authentication-service",
    indexes = {
      @Index(name = "idx_users_username_password", columnList = "username, password"),
      @Index(name = "idx_users_email_password", columnList = "email, password")
    },
    uniqueConstraints = {
      @UniqueConstraint(
          name = "users_username_key",
          columnNames = {"username"}),
      @UniqueConstraint(
          name = "users_email_key",
          columnNames = {"email"})
    })
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @ColumnDefault("gen_random_uuid()")
  @Column(name = "id", nullable = false)
  private UUID id;

  @Size(max = 50)
  @NotNull
  @Column(name = "username", nullable = false, length = 50)
  private String username;

  @Size(max = 100)
  @NotNull
  @Column(name = "email", nullable = false, length = 100)
  private String email;

  @NotNull
  @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
  private String password;

  @Size(max = 50)
  @Column(name = "first_name", length = 50)
  private String firstName;

  @Size(max = 50)
  @Column(name = "last_name", length = 50)
  private String lastName;

  @Size(max = 20)
  @Column(name = "phone_number", length = 20)
  private String phoneNumber;

  @ColumnDefault("false")
  @Column(name = "enabled")
  private Boolean enabled;

  @ColumnDefault("false")
  @Column(name = "email_verified")
  private Boolean emailVerified;

  @ColumnDefault("false")
  @Column(name = "phone_verified")
  private Boolean phoneVerified;

  @Size(max = 10)
  @Column(name = "locale", length = 10)
  private String locale;

  @Size(max = 50)
  @Column(name = "timezone", length = 50)
  private String timezone;

  @NotNull
  @ColumnDefault("now()")
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @NotNull
  @ColumnDefault("now()")
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  @Column(name = "last_login")
  private OffsetDateTime lastLogin;

  @Column(name = "password_change_at")
  private OffsetDateTime passwordChangeAt;
}
