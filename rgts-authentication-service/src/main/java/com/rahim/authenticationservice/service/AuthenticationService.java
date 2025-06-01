package com.rahim.authenticationservice.service;

import com.rahim.authenticationservice.dto.internal.AuthResult;
import com.rahim.authenticationservice.dto.request.LoginUserRequest;
import com.rahim.authenticationservice.dto.request.SignupUserRequest;
import com.rahim.authenticationservice.dto.request.VerifyUserRequest;
import com.rahim.authenticationservice.entity.RefreshToken;
import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.exception.VerificationException;
import com.rahim.authenticationservice.repository.RefreshTokenRepository;
import com.rahim.authenticationservice.repository.UserRepository;
import com.rahim.common.exception.DuplicateEntityException;
import com.rahim.common.exception.EntityNotFoundException;
import com.rahim.kafkaservice.service.IKafkaService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AuthenticationService {
  private final IKafkaService kafkaService;
  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public User signup(SignupUserRequest signupUserRequest) {
    if (userExists(signupUserRequest)) {
      throw new DuplicateEntityException("Account with username/email already exists");
    }

    User user =
        User.builder()
            .username(signupUserRequest.getUsername())
            .email(signupUserRequest.getEmail())
            .password(passwordEncoder.encode(signupUserRequest.getPassword()))
            .build();

    user.setVerificationCode(generateVerificationCode());
    user.setVerificationExpiry(getVerificationExpiry());
    user.setEnabled(false);
    sendVerificationEmail(user);
    return userRepository.save(user);
  }

  public void verifyUser(VerifyUserRequest verifyUserRequest) {
    User user = findByUsername(verifyUserRequest.getUsername());

    if (user.getVerificationExpiry().isBefore(Instant.now())) {
      throw new VerificationException("Verification code has expired");
    }

    if (user.getVerificationCode().equals(verifyUserRequest.getVerificationCode())) {
      user.setEnabled(true);
      user.setVerificationCode(null);
      user.setVerificationExpiry(null);
      userRepository.save(user);
    } else {
      throw new VerificationException("Invalid verification code");
    }
  }

  public AuthResult login(LoginUserRequest loginUserRequest) {
    User user = findByUsername(loginUserRequest.getUsername());

    if (!user.isEnabled()) {
      throw new VerificationException("Account is not verified");
    }

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginUserRequest.getUsername(), loginUserRequest.getPassword()));

    String refreshToken = jwtService.generateRefreshToken(user);
    RefreshToken tokenEntity =
        RefreshToken.builder()
            .token(refreshToken)
            .userId(user.getId())
            .expiresAt(Instant.now().plusMillis(jwtService.getRefreshExpirationTime()))
            .createdAt(Instant.now())
            .revoked(false)
            .build();
    refreshTokenRepository.save(tokenEntity);

    return new AuthResult(user, refreshToken);
  }

  public void resendVerificationEmail(String username) {
    User user = findByUsername(username);
    if (user.isEnabled()) {
      throw new VerificationException("Account is already verified");
    }

    user.setVerificationCode(generateVerificationCode());
    user.setVerificationExpiry(getVerificationExpiry());
    sendVerificationEmail(user);
    userRepository.save(user);
  }

  public User findByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(
            () -> new EntityNotFoundException("User not found with username: " + username));
  }

  public void logout(String refreshToken) {
    try {
      logoutWithDatabase(refreshToken);
      log.info("User logged out successfully");
    } catch (Exception e) {
      log.error("Logout failed: {}", e.getMessage());
      throw new RuntimeException("Logout failed", e);
    }
  }

  private void logoutWithDatabase(String refreshToken) {
    try {
      String username = jwtService.extractUsername(refreshToken);
      User user = findByUsername(username);

      if (!jwtService.isValidRefreshToken(refreshToken, user)) {
        throw new IllegalArgumentException("Invalid refresh token");
      }

      Optional<RefreshToken> tokenEntity =
          refreshTokenRepository.findByTokenAndUserId(refreshToken, user.getId());

      if (tokenEntity.isPresent()) {
        RefreshToken token = tokenEntity.get();
        token.setRevoked(true);
        token.setRevokedAt(Instant.now());
        refreshTokenRepository.save(token);
      } else {
        RefreshToken revokedToken =
            RefreshToken.builder()
                .token(refreshToken)
                .userId(user.getId())
                .revoked(true)
                .revokedAt(Instant.now())
                .expiresAt(
                    Instant.now().plus(jwtService.getRefreshExpirationTime(), ChronoUnit.MILLIS))
                .build();
        refreshTokenRepository.save(revokedToken);
      }

      log.info("Refresh token revoked in database for user: {}", username);

    } catch (Exception e) {
      log.error("Database logout failed: {}", e.getMessage());
      throw new RuntimeException("Failed to logout with database strategy", e);
    }
  }

  public void logoutFromAllDevices(String username) {
    try {
      User user = findByUsername(username);
      if (refreshTokenRepository != null) {
        refreshTokenRepository.revokeAllTokensForUser(user.getId());
        log.info("All refresh tokens revoked for user: {}", username);
      } else {
        log.warn("Cannot logout from all devices - refresh token repository not available");
        throw new UnsupportedOperationException(
            "Logout from all devices not supported without database");
      }

    } catch (Exception e) {
      log.error("Failed to logout from all devices: {}", e.getMessage());
      throw new RuntimeException("Failed to logout from all devices", e);
    }
  }

  public boolean isRefreshTokenRevoked(String refreshToken) {
    if (refreshTokenRepository == null) {
      return false;
    }

    try {
      String username = jwtService.extractUsername(refreshToken);
      User user = findByUsername(username);

      Optional<RefreshToken> tokenEntity =
          refreshTokenRepository.findByTokenAndUserId(refreshToken, user.getId());
      return tokenEntity.map(RefreshToken::isRevoked).orElse(false);

    } catch (Exception e) {
      log.error("Failed to check token revocation status: {}", e.getMessage());
      return true;
    }
  }

  @Scheduled(cron = "0 0 0 * * ?")
  public void cleanupExpiredTokens() {
    if (refreshTokenRepository != null) {
      int deletedCount = refreshTokenRepository.deleteExpiredTokens(Instant.now());
      log.info("Cleaned up {} expired refresh tokens", deletedCount);
    }
  }

  private boolean userExists(SignupUserRequest signupUserRequest) {
    return userRepository.existsByUsername(signupUserRequest.getUsername())
        || userRepository.existsByEmail(signupUserRequest.getEmail());
  }

  private Instant getVerificationExpiry() {
    return Instant.now().plus(15, ChronoUnit.MINUTES);
  }

  private String generateVerificationCode() {
    return String.format("%06d", (int) (Math.random() * 1000000));
  }

  private void sendVerificationEmail(User user) {
    System.out.println(user);
  }
}
