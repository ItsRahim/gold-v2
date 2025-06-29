package com.rahim.authenticationservice.service.authentication;

import com.rahim.authenticationservice.dto.request.RegisterRequest;
import com.rahim.authenticationservice.dto.request.VerificationRequest;
import com.rahim.authenticationservice.dto.response.RegisterResponse;
import com.rahim.authenticationservice.dto.response.VerificationResponse;
import com.rahim.authenticationservice.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

/**
 * @created 09/06/2025
 * @author Rahim Ahmed
 */
public interface IAuthenticationService {
  RegisterResponse register(RegisterRequest registerRequest, HttpServletRequest request);

  VerificationResponse verifyEmail(
      VerificationRequest verificationRequest, HttpServletRequest request);

  VerificationResponse verifyEmail(
      String verificationCode, UUID verificationId, HttpServletRequest request);

  Optional<User> findByUsername(String username);
}
