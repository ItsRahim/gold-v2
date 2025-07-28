package com.rahim.authenticationservice.service.verification;

import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.enums.VerificationType;
import java.util.UUID;

public interface IVerificationService {
  void sendEmailVerification(User user);

  boolean verifyEmail(UUID userId, String token);

  UUID verifyCode(String token, UUID verificationId, VerificationType verificationType);

  void regenerateEmailToken(UUID userId);

  void generateEmailToken(UUID userId);

  boolean isEmailVerified(UUID userId);

  boolean isPhoneVerified(UUID userId);
}
