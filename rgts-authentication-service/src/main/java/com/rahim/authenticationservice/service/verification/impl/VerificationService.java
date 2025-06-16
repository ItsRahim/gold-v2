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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
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
  private final IKafkaService kafkaService;

  private static final int VERIFICATION_CODE_EXPIRATION_MINUTES = 30;
  private static final int VERIFICATION_CODE_LENGTH = 6;
  private final BCryptPasswordEncoder passwordEncoder;

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
    } catch (Exception e) {
      log.error("Failed to create verification code for user: {}", e.getMessage());
      throw new VerificationException("Failed to create verification code for user");
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
}
