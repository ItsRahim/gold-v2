package com.rahim.authenticationservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.*;
import org.hibernate.Hibernate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class UserRoleId implements Serializable {
  private static final long serialVersionUID = -6201596793066185045L;

  @NotNull
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Size(max = 20)
  @NotNull
  @Column(name = "role", nullable = false, length = 20)
  private String role;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    UserRoleId entity = (UserRoleId) o;
    return Objects.equals(this.role, entity.role) && Objects.equals(this.userId, entity.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(role, userId);
  }
}
