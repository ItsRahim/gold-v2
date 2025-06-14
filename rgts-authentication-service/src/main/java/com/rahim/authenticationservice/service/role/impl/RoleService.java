package com.rahim.authenticationservice.service.role.impl;

import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.entity.UserRole;
import com.rahim.authenticationservice.enums.Role;
import com.rahim.authenticationservice.repository.UserRoleRepository;
import com.rahim.authenticationservice.service.role.IRoleService;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class RoleService implements IRoleService {
  private final UserRoleRepository userRoleRepository;

  @Override
  public UserRole createRole(String name, String description) {
    return null;
  }

  @Override
  public void assignRoleToUser(User userId, Role role) {
    UserRole userRole = UserRole.builder().user(userId).role(role).build();
    userRoleRepository.save(userRole);
  }

  @Override
  public void removeRoleFromUser(UUID userId, String roleName) {}

  @Override
  public Set<UserRole> getUserRoles(UUID userId) {
    return Set.of();
  }

  @Override
  public boolean hasRole(UUID userId, String roleName) {
    return false;
  }

  @Override
  public Set<UserRole> getDefaultRoles() {
    return Set.of();
  }
}
