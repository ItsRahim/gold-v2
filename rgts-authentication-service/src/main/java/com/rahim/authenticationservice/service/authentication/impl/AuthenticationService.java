package com.rahim.authenticationservice.service.authentication.impl;

import com.rahim.authenticationservice.dto.enums.ResponseStatus;
import com.rahim.authenticationservice.dto.request.RegisterRequest;
import com.rahim.authenticationservice.dto.request.VerificationRequest;
import com.rahim.authenticationservice.dto.response.RegisterResponse;
import com.rahim.authenticationservice.dto.response.UserData;
import com.rahim.authenticationservice.dto.response.VerificationResponse;
import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.enums.Role;
import com.rahim.authenticationservice.enums.VerificationType;
import com.rahim.authenticationservice.repository.UserRepository;
import com.rahim.authenticationservice.service.authentication.IAuthenticationService;
import com.rahim.authenticationservice.service.role.IRoleService;
import com.rahim.authenticationservice.service.verification.IVerificationService;
import com.rahim.authenticationservice.util.EmailFormatUtil;
import com.rahim.authenticationservice.util.RequestUtils;
import com.rahim.common.exception.*;
import com.rahim.common.util.DateUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class AuthenticationService implements IAuthenticationService {

  private final UserRepository userRepository;
  private final IRoleService roleService;
  private final IVerificationService verificationService;
  private final BCryptPasswordEncoder passwordEncoder;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public RegisterResponse register(RegisterRequest registerRequest, HttpServletRequest request) {
    log.info("Starting registration for user: {}", registerRequest.getUsername());

    validateRegisterRequest(registerRequest);
    checkUserUniqueness(registerRequest);

    User user = createUser(registerRequest, request);
    log.info("User created with ID: {}", user.getId());

    roleService.assignRoleToUser(user, Role.USER);
    log.debug("Assigned USER role to user ID: {}", user.getId());

    try {
      verificationService.sendEmailVerification(user);
      log.info("Email verification sent for user ID: {}", user.getId());
    } catch (Exception e) {
      log.error("Failed to send verification for user ID {}: {}", user.getId(), e.getMessage());
      throw new ServiceException("Failed to complete registration");
    }

    return RegisterResponse.builder()
        .message("User registered successfully. Please check your email to verify your account.")
        .status(ResponseStatus.PENDING)
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .build();
  }

  @Override
  public VerificationResponse verifyEmail(
      VerificationRequest verificationRequest, HttpServletRequest request) {
    String email = verificationRequest.getEmail();
    String verificationCode = verificationRequest.getVerificationCode();

    if (EmailFormatUtil.isInvalidEmail(email)) {
      log.error("Invalid email: {}", email);
      throw new BadRequestException("Invalid email format provided");
    }

    if (StringUtils.isBlank(verificationCode)) {
      log.error("Missing verification code for email: {}", email);
      throw new BadRequestException("Verification code is required");
    }

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

    if (user.isEmailVerified()) {
      log.warn("Email already verified: {}", email);
      throw new ResourceConflictException("User with email " + email + " is already verified");
    }

    try {
      boolean isVerified = verificationService.verifyEmail(user.getId(), verificationCode);

      if (!isVerified) {
        log.warn("Verification failed for email: {}", email);
        throw new BadRequestException(
            "Verification code does not match for user with email: " + email);
      }

      updateUserAfterVerification(user);
      return buildVerificationResponse(user);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Verification error for email {}: {}", email, e.getMessage(), e);
      throw new ServiceException("Failed to verify email");
    }
  }

  @Override
  public VerificationResponse verifyEmail(String verificationCode, UUID verificationId, HttpServletRequest request) {
    try {
      UUID userId = verificationService.verifyCode(verificationCode, verificationId, VerificationType.EMAIL);

      User user =
          userRepository
              .findById(userId)
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
                          "User not found with ID: " + userId + ". Unable to verify email."));

      if (user.isEmailVerified()) {
        log.warn("Email already verified: {}", user.getEmail());
        throw new ResourceConflictException(
            "User with email " + user.getEmail() + " is already verified");
      }

      updateUserAfterVerification(user);
      return buildVerificationResponse(user);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Verification error with token: {} - {}", verificationCode, e.getMessage(), e);
      throw new ServiceException("Failed to verify email");
    }
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  // ------------------------ Private Helpers ------------------------

  private void validateRegisterRequest(RegisterRequest request) {
    if (EmailFormatUtil.isInvalidEmail(request.getEmail())) {
      throw new BadRequestException("Invalid email format provided");
    }

    if (StringUtils.isBlank(request.getUsername())) {
      throw new BadRequestException("Username is required");
    }

    if (StringUtils.isBlank(request.getPassword())) {
      throw new BadRequestException("Password is required");
    }

    if (StringUtils.isBlank(request.getFirstName())) {
      throw new BadRequestException("First name is required");
    }

    if (StringUtils.isBlank(request.getLastName())) {
      throw new BadRequestException("Last name is required");
    }

    String phone = request.getPhoneNumber();
    if (StringUtils.isBlank(phone)) {
      throw new BadRequestException("Phone number is required");
    }
  }

  private void checkUserUniqueness(RegisterRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new DuplicateEntityException(
          "User with username " + request.getUsername() + " already exists.");
    }

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new DuplicateEntityException(
          "User with email " + request.getEmail() + " already exists.");
    }

    if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
      throw new DuplicateEntityException(
          "User with phone number " + request.getPhoneNumber() + " already exists.");
    }
  }

  private User createUser(RegisterRequest request, HttpServletRequest httpRequest) {
    return userRepository.save(
        User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .phoneNumber(request.getPhoneNumber())
            .emailVerified(false)
            .phoneVerified(false)
            .locale(RequestUtils.getClientLocale(httpRequest))
            .timezone(RequestUtils.getClientTimezone(httpRequest))
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .accountExpired(false)
            .accountLocked(true)
            .build());
  }

  private void updateUserAfterVerification(User user) {
    user.setEmailVerified(true);
    user.setAccountLocked(false);
    user.setUpdatedAt(DateUtil.nowUtc());
    userRepository.save(user);
  }

  private VerificationResponse buildVerificationResponse(User user) {
    UserData userData =
        UserData.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .verifiedAt(DateUtil.formatOffsetDateTime(user.getUpdatedAt()))
            .build();

    return VerificationResponse.builder()
        .status(ResponseStatus.SUCCESS)
        .message("Email verified successfully")
        .userData(userData)
        .build();
  }
}
