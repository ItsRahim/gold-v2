package com.rahim.authenticationservice.service.verification.impl;

import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.entity.VerificationCode;
import com.rahim.authenticationservice.enums.VerificationType;
import com.rahim.authenticationservice.repository.VerificationCodeRepository;
import com.rahim.authenticationservice.service.verification.IVerificationService;
import com.rahim.common.exception.BadRequestException;
import com.rahim.common.exception.EntityNotFoundException;
import com.rahim.common.exception.ServiceException;
import com.rahim.common.util.DateUtil;
import com.rahim.kafkaservice.service.IKafkaService;
import com.rahim.proto.protobuf.email.AccountVerificationData;
import com.rahim.proto.protobuf.email.EmailRequest;
import com.rahim.proto.protobuf.email.EmailTemplate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class VerificationService implements IVerificationService {

  private final VerificationCodeRepository verificationCodeRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final IKafkaService kafkaService;

  @Value("${verification.email.expiration-minutes:30}")
  private int emailCodeExpirationMinutes;

  @Value("${verification.code.length:6}")
  private int verificationCodeLength;

  @Value("${verification.max-attempts:5}")
  private int maxVerificationAttempts;

  @Override
  public void sendEmailVerification(User user) {
    log.debug("Starting email verification for user: {}", user.getId());

    String rawCode = generateVerificationCode();
    String hashedCode = passwordEncoder.encode(rawCode);
    OffsetDateTime now = DateUtil.nowUtc();
    OffsetDateTime expiresAt = DateUtil.addMinutesToNowUtc(emailCodeExpirationMinutes);

    VerificationCode verificationCode =
        VerificationCode.builder()
            .user(user)
            .code(hashedCode)
            .type(VerificationType.EMAIL)
            .createdAt(now)
            .expiresAt(expiresAt)
            .attempts(0)
            .build();

    verificationCodeRepository.save(verificationCode);
    log.debug("Saved verification code for user: {}", user.getId());

    sendEmail(user, rawCode, hashedCode, expiresAt);
    log.info("Verification email sent to user: {}", user.getId());
  }

  @Override
  public void sendPhoneVerification(User user) {}

  @Override
  @Transactional(noRollbackFor = BadRequestException.class)
  public boolean verifyEmail(UUID userId, String token) {
    return verifyCodeWithUserId(userId, token, VerificationType.EMAIL);
  }

  @Override
  @Transactional(noRollbackFor = BadRequestException.class)
  public boolean verifyPhone(UUID userId, String token) {
    return verifyCodeWithUserId(userId, token, VerificationType.PHONE);
  }

  @Override
  @Transactional(noRollbackFor = BadRequestException.class)
  public UUID verifyCode(String hashedToken, VerificationType type) {
    return verifyCodeWithHashedToken(hashedToken, type);
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

  private String generateVerificationCode() {
    return RandomStringUtils.randomAlphanumeric(verificationCodeLength).toUpperCase();
  }

  private void sendEmail(User user, String rawCode, String hashedCode, OffsetDateTime expiresAt) {
    try {
      AccountVerificationData verificationData =
          AccountVerificationData.newBuilder()
              .setRawVerificationCode(rawCode)
              .setHashedVerificationCode(hashedCode)
              .setExpirationTime(DateUtil.formatOffsetDateTime(expiresAt))
              .build();

      EmailRequest request =
          EmailRequest.newBuilder()
              .setRecipientEmail(user.getEmail())
              .setTemplate(EmailTemplate.VERIFICATION_REQUEST)
              .setFirstName(user.getFirstName())
              .setLastName(user.getLastName())
              .setUsername(user.getUsername())
              .setVerificationData(verificationData)
              .build();

      kafkaService.sendMessage("email-request", request);
      log.info("Verification email sent for user: {}", user.getUsername());
    } catch (Exception e) {
      log.error("Error sending verification email: {}", e.getMessage(), e);
      throw new ServiceException("Failed to send verification code");
    }
  }

  private boolean verifyCodeWithUserId(UUID userId, String token, VerificationType type) {
    if (StringUtils.isBlank(token)) {
      throw new BadRequestException("Verification token is missing.");
    }

    VerificationCode code =
        verificationCodeRepository
            .findByUserIdAndType(userId, type)
            .orElseThrow(
                () ->
                    new EntityNotFoundException("Verification code not found. Request a new one."));

    validateAttempts(code);
    validateToken(token, code.getCode());
    validateExpiration(code.getExpiresAt());

    deleteVerificationCode(code);
    log.info("User {} successfully verified for type {}", userId, type);
    return true;
  }

  private UUID verifyCodeWithHashedToken(String hashedToken, VerificationType type) {
    if (StringUtils.isBlank(hashedToken)) {
      throw new BadRequestException("Verification token is missing.");
    }

    VerificationCode code =
        verificationCodeRepository
            .findByCodeAndType(hashedToken, type)
            .orElseThrow(
                () -> new EntityNotFoundException("Invalid or expired verification token."));

    UUID userId = code.getUser().getId();
    if (userId == null) {
      throw new BadRequestException("Verification code does not belong to any user.");
    }

    validateAttempts(code);
    validateExpiration(code.getExpiresAt());

    deleteVerificationCode(code);
    log.info("User {} successfully verified via hashed token for type {}", userId, type);
    return userId;
  }

  private void validateAttempts(VerificationCode code) {
    if (code.getAttempts() >= maxVerificationAttempts) {
      deleteVerificationCode(code);
      throw new BadRequestException("Max verification attempts exceeded. Request a new code.");
    }
    code.setAttempts(code.getAttempts() + 1);
    verificationCodeRepository.save(code);
  }

  private void validateToken(String token, String hashedCode) {
    if (!passwordEncoder.matches(token.trim().toUpperCase(), hashedCode)) {
      throw new BadRequestException("Verification token does not match.");
    }
  }

  private void validateExpiration(OffsetDateTime expiresAt) {
    if (expiresAt.isBefore(DateUtil.nowUtc())) {
      throw new BadRequestException("Verification code has expired. Please request a new one.");
    }
  }

  private void deleteVerificationCode(VerificationCode code) {
    try {
      verificationCodeRepository.delete(code);
      log.debug("Deleted verification code: {}", code.getId());
    } catch (Exception e) {
      log.error("Failed to delete verification code: {}", e.getMessage(), e);
      throw new ServiceException("Failed to delete verification code");
    }
  }
}
