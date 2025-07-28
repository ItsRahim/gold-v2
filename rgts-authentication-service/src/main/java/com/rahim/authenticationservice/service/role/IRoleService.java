package com.rahim.authenticationservice.service.role;

import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.entity.UserRole;
import com.rahim.authenticationservice.enums.Role;
import java.util.Set;
import java.util.UUID;

public interface IRoleService {
  void assignRoleToUser(User user, Role role);
}
