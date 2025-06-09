package com.rahim.authenticationservice.service.role;

import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.entity.UserRole;
import com.rahim.authenticationservice.enums.Role;
import java.util.Set;
import java.util.UUID;

public interface IRoleService {
  UserRole createRole(String name, String description);

  void assignRoleToUser(User user, Role role);

  void removeRoleFromUser(UUID userId, String roleName);

  Set<UserRole> getUserRoles(UUID userId);

  boolean hasRole(UUID userId, String roleName);

  Set<UserRole> getDefaultRoles();
}
