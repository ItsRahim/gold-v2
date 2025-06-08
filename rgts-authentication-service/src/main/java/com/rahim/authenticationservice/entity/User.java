package com.rahim.authenticationservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class User implements UserDetails {
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

  @NotNull
  @Size(max = 50)
  @Column(name = "first_name", length = 50)
  private String firstName;

  @NotNull
  @Size(max = 50)
  @Column(name = "last_name", length = 50)
  private String lastName;

  @Size(max = 20)
  @Column(name = "phone_number", length = 20)
  private String phoneNumber;

  @ColumnDefault("false")
  @Column(name = "enabled", nullable = false)
  private boolean enabled;

  @NotNull
  @ColumnDefault("false")
  @Column(name = "email_verified", nullable = false)
  private boolean emailVerified;

  @NotNull
  @ColumnDefault("false")
  @Column(name = "phone_verified", nullable = false)
  private boolean phoneVerified;

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

  @NotNull
  @ColumnDefault("false")
  @Column(name = "account_expired", nullable = false)
  private boolean accountExpired;

  @NotNull
  @ColumnDefault("false")
  @Column(name = "account_locked", nullable = false)
  private boolean accountLocked;

  @Column(name = "password_change_at")
  private OffsetDateTime passwordChangeAt;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private Set<UserRole> userRoles = new HashSet<>();

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return userRoles.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole().name()))
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isAccountNonExpired() {
    return !accountExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !accountLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    if (passwordChangeAt == null) {
      return true;
    }

    OffsetDateTime expirationThreshold = passwordChangeAt.plusDays(90);
    return OffsetDateTime.now().isBefore(expirationThreshold);
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
