package com.rahim.authenticationservice.service.verification;

import com.rahim.authenticationservice.entity.User;
import java.util.UUID;

public interface IVerificationService {
  void sendEmailVerification(User user);

  void sendPhoneVerification(User user);

  boolean verifyEmail(UUID userId, String token);

  boolean verifyPhone(UUID userId, String token);

  void regenerateEmailToken(UUID userId);

  void generateEmailToken(UUID userId);

  void regeneratePhoneCode(UUID userId);

  void generatePhoneCode(UUID userId);

  boolean isEmailVerified(UUID userId);

  boolean isPhoneVerified(UUID userId);

  void checkVerificationAttempts(UUID userId);
}
