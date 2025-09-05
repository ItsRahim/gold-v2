package com.rahim.authenticationservice.service.role;

import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.enums.Role;

public interface IRoleService {
  void assignRoleToUser(User user, Role role);
}
