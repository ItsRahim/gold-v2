package com.rahim.authenticationservice.service.security.impl;

import com.rahim.authenticationservice.service.security.ISecurityService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class SecurityService implements ISecurityService {

  @Override
  public void checkAccountStatus(UUID userId) {}

  @Override
  public void lockAccount(UUID userId) {}

  @Override
  public void unlockAccount(UUID userId) {}

  @Override
  public void resetFailedAttempts(UUID userId) {}

  @Override
  public void incrementFailedAttempts(UUID userId) {}

  @Override
  public boolean isAccountLocked(UUID userId) {
    return false;
  }

  @Override
  public void updateLastLogin(UUID userId) {}

  @Override
  public void requirePasswordChange(UUID userId) {}
}
