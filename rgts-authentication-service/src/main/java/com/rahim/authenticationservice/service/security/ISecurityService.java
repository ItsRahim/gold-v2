package com.rahim.authenticationservice.service.security;

import java.util.UUID;

public interface ISecurityService {
  void checkAccountStatus(UUID userId);

  void lockAccount(UUID userId);

  void unlockAccount(UUID userId);

  void resetFailedAttempts(UUID userId);

  void incrementFailedAttempts(UUID userId);

  boolean isAccountLocked(UUID userId);

  void updateLastLogin(UUID userId);

  void requirePasswordChange(UUID userId);
}
