package com.rahim.authenticationservice.service.verification.impl;

import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.entity.VerificationCode;
import com.rahim.authenticationservice.enums.VerificationType;
import com.rahim.authenticationservice.exception.VerificationException;
import com.rahim.authenticationservice.repository.VerificationCodeRepository;
import com.rahim.authenticationservice.service.verification.IVerificationService;
import com.rahim.common.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * @created 09/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class VerificationService implements IVerificationService {
  private final VerificationCodeRepository verificationCodeRepository;

  private static final int VERIFICATION_CODE_EXPIRATION_MINUTES = 30;
  private static final int VERIFICATION_CODE_LENGTH = 6;

  @Override
  public void sendEmailVerification(User user) {
    try {
      log.debug("Starting email verification for user: {}", user.getId());
      String verificationCode = generateVerificationCode();
      OffsetDateTime now = DateUtil.nowUtc();
      OffsetDateTime expiresAt = DateUtil.addMinutesToNowUtc(VERIFICATION_CODE_EXPIRATION_MINUTES);

      log.debug("Generated verification code. Created at: {}, Expires at: {}", now, expiresAt);

      VerificationCode verificationCodeEntity =
          VerificationCode.builder()
              .user(user)
              .code(verificationCode)
              .type(VerificationType.EMAIL)
              .createdAt(now)
              .expiresAt(expiresAt)
              .attempts(0)
              .build();

      log.debug("Built verification code entity: {}", verificationCodeEntity);
      verificationCodeRepository.save(verificationCodeEntity);
      log.debug("Successfully saved verification code");

    } catch (Exception e) {
      log.error(
          "Failed to create verification code for user: {}. Error: {}",
          user.getId(),
          e.getMessage(),
          e);
      throw new VerificationException("Failed to create verification code", e);
    }
  }

  @Override
  public void sendPhoneVerification(User user) {}

  @Override
  public boolean verifyEmail(String token) {
    return false;
  }

  @Override
  public boolean verifyPhone(UUID userId, String code) {
    return false;
  }

  @Override
  public void regenerateEmailToken(UUID userId) {}

  @Override
  public void generateEmailToken(UUID userId) {}

  @Override
  public void regeneratePhoneCode(UUID userId) {}

  @Override
  public void generatePhoneCode(UUID userId) {}

  @Override
  public boolean isEmailVerified(UUID userId) {
    return false;
  }

  @Override
  public boolean isPhoneVerified(UUID userId) {
    return false;
  }

  @Override
  public void checkVerificationAttempts(UUID userId) {}

  private String generateVerificationCode() {
    return RandomStringUtils.randomAlphanumeric(VERIFICATION_CODE_LENGTH).toUpperCase();
  }
}
