package com.rahim.authenticationservice.service.authentication;

import com.rahim.authenticationservice.dto.request.AuthRequest;
import com.rahim.authenticationservice.dto.request.RegisterRequest;
import com.rahim.authenticationservice.dto.request.EmailVerificationRequest;
import com.rahim.authenticationservice.dto.response.AuthResponse;
import com.rahim.authenticationservice.dto.response.RegisterResponse;
import com.rahim.authenticationservice.dto.response.EmailVerificationResponse;
import com.rahim.authenticationservice.entity.User;
import com.rahim.jwtcore.response.TokenVerificationResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @created 09/06/2025
 * @author Rahim Ahmed
 */
public interface IAuthenticationService {
  RegisterResponse register(RegisterRequest registerRequest, HttpServletRequest request);

  EmailVerificationResponse verifyEmail(
          EmailVerificationRequest emailVerificationRequest, HttpServletRequest request);

  Optional<User> findByUsername(String username);

  AuthResponse login(AuthRequest authRequest, HttpServletRequest request);

  TokenVerificationResponse verifyToken(String authHeader);
}
