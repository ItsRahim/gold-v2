package com.rahim.authenticationservice.service.verification.impl;

import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.entity.VerificationCode;
import com.rahim.authenticationservice.enums.VerificationType;
import com.rahim.authenticationservice.exception.SendEmailException;
import com.rahim.authenticationservice.exception.VerificationException;
import com.rahim.authenticationservice.repository.VerificationCodeRepository;
import com.rahim.authenticationservice.service.verification.IVerificationService;
import com.rahim.common.util.DateUtil;
import com.rahim.kafkaservice.service.IKafkaService;
import com.rahim.proto.protobuf.email.AccountVerificationData;
import com.rahim.proto.protobuf.email.EmailRequest;
import com.rahim.proto.protobuf.email.EmailTemplate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  private final BCryptPasswordEncoder passwordEncoder;
  private final IKafkaService kafkaService;

  private static final int VERIFICATION_CODE_EXPIRATION_MINUTES = 30;
  private static final int VERIFICATION_CODE_LENGTH = 6;
  private static final int MAX_VERIFICATION_ATTEMPTS = 5;

  @Override
  public void sendEmailVerification(User user) {
    try {
      log.debug("Starting email verification for user: {}", user.getId());
      String verificationCode = generateVerificationCode();
      String hashedVerificationCode = passwordEncoder.encode(verificationCode);
      OffsetDateTime now = DateUtil.nowUtc();
      OffsetDateTime expiresAt = DateUtil.addMinutesToNowUtc(VERIFICATION_CODE_EXPIRATION_MINUTES);

      VerificationCode verificationCodeEntity =
          VerificationCode.builder()
              .user(user)
              .code(hashedVerificationCode)
              .type(VerificationType.EMAIL)
              .createdAt(now)
              .expiresAt(expiresAt)
              .attempts(0)
              .build();

      verificationCodeRepository.save(verificationCodeEntity);
      log.debug("Successfully saved verification code");

      sendEmail(user, verificationCode, expiresAt);
      log.info("Email verification code sent successfully for user: {}", user.getId());
    } catch (Exception e) {
      log.error("Failed to create verification code for user: {}", e.getMessage());
      throw new VerificationException("Failed to create verification code for user");
    }
  }

  @Override
  public void sendPhoneVerification(User user) {}

  @Override
  @Transactional(noRollbackFor = VerificationException.class)
  public boolean verifyEmail(UUID userId, String token) {
    return verifyCode(userId, token, VerificationType.EMAIL);
  }

  @Override
  @Transactional(noRollbackFor = VerificationException.class)
  public boolean verifyPhone(UUID userId, String token) {
    return verifyCode(userId, token, VerificationType.PHONE);
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

  private void sendEmail(User user, String verificationCode, OffsetDateTime expiresAt) {
    try {
      String email = user.getEmail();
      String firstName = user.getFirstName();
      String lastName = user.getLastName();
      String username = user.getUsername();
      String expirationTime = DateUtil.formatOffsetDateTime(expiresAt);

      AccountVerificationData accountVerificationData =
          AccountVerificationData.newBuilder()
              .setVerificationCode(verificationCode)
              .setExpirationTime(expirationTime)
              .build();

      EmailRequest emailRequest =
          EmailRequest.newBuilder()
              .setRecipientEmail(email)
              .setTemplate(EmailTemplate.VERIFICATION_REQUEST)
              .setFirstName(firstName)
              .setLastName(lastName)
              .setUsername(username)
              .setVerificationData(accountVerificationData)
              .build();

      kafkaService.sendMessage("email-request", emailRequest);
      log.info("Verification code sent successfully to user: {}", username);
    } catch (Exception e) {
      log.error("Failed to send verification code: {}", e.getMessage(), e);
      throw new SendEmailException("Failed to send verification code");
    }
  }

  private boolean verifyCode(UUID userId, String token, VerificationType type) {
    try {
      log.debug("Verifying verification code for user: {}", userId);

      if (StringUtils.isEmpty(token)) {
        log.error("Verification token is empty for user: {}", userId);
        throw new VerificationException("Verification token is empty");
      }

      Optional<VerificationCode> optionalVerificationCode =
          verificationCodeRepository.findByUserIdAndType(userId, type);

      if (optionalVerificationCode.isEmpty()) {
        log.warn("No verification code found for user: {}", userId);
        throw new VerificationException("Verification code not found. Please request a new code.");
      }

      VerificationCode verificationCode = optionalVerificationCode.get();

      int verificationCodeAttempts = verificationCode.getAttempts();
      if (verificationCodeAttempts >= MAX_VERIFICATION_ATTEMPTS) {
        log.warn("Maximum verification attempts exceeded for user: {}", userId);
        deleteVerificationCode(verificationCode);
        throw new VerificationException(
            "Maximum verification attempts exceeded. Request a new code.");
      }

      verificationCode.setAttempts(verificationCodeAttempts + 1);
      verificationCodeRepository.save(verificationCode);

      if (!passwordEncoder.matches(token.trim().toUpperCase(), verificationCode.getCode())) {
        log.warn("Verification code does not match for user: {}", userId);
        verificationCodeRepository.save(verificationCode);
        throw new VerificationException("Verification code does not match");
      }

      if (verificationCode.getExpiresAt().isBefore(DateUtil.nowUtc())) {
        log.warn("Verification code expired for user: {}", userId);
        deleteVerificationCode(verificationCode);
        throw new VerificationException("Verification code expired. Please request a new code.");
      }

      deleteVerificationCode(verificationCode);
      return true;

    } catch (VerificationException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error verifying code for user {}: {}", userId, e.getMessage(), e);
      throw new VerificationException("Error verifying code for user: " + userId);
    }
  }

  private void deleteVerificationCode(VerificationCode verificationCode) {
    log.debug("Deleting verification code: {}", verificationCode.getId());
    try {
      verificationCodeRepository.delete(verificationCode);
      log.info("Successfully deleted verification code: {}", verificationCode.getId());
    } catch (Exception e) {
      log.error("Failed to delete verification code: {}", e.getMessage(), e);
      throw new VerificationException("Failed to delete verification code");
    }
  }
}
