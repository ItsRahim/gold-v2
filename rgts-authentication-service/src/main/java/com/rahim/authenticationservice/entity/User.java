package com.rahim.authenticationservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", schema = "authentication-service")
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Size(max = 50)
  @NotNull
  @Column(name = "username", unique = true, nullable = false, length = 50)
  private String username;

  @Size(max = 100)
  @NotNull
  @Column(name = "email", unique = true, nullable = false, length = 100)
  private String email;

  @NotNull
  @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
  private String password;

  @NotNull
  @ColumnDefault("true")
  @Column(name = "enabled", nullable = false)
  private boolean enabled;

  @Size(max = 10)
  @Column(name = "verification_code", length = 10)
  private String verificationCode;

  @Column(name = "verification_expiry")
  private Instant verificationExpiry;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }
}
