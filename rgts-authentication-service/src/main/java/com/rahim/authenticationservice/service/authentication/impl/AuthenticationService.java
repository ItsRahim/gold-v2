package com.rahim.authenticationservice.service.authentication.impl;

import com.rahim.authenticationservice.dto.enums.ResponseStatus;
import com.rahim.authenticationservice.dto.request.RegisterRequest;
import com.rahim.authenticationservice.dto.request.VerificationRequest;
import com.rahim.authenticationservice.dto.response.RegisterResponse;
import com.rahim.authenticationservice.dto.response.UserData;
import com.rahim.authenticationservice.dto.response.VerificationResponse;
import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.enums.Role;
import com.rahim.authenticationservice.exception.VerificationException;
import com.rahim.authenticationservice.repository.UserRepository;
import com.rahim.authenticationservice.service.authentication.IAuthenticationService;
import com.rahim.authenticationservice.service.role.IRoleService;
import com.rahim.authenticationservice.service.verification.IVerificationService;
import com.rahim.authenticationservice.util.RequestUtils;
import com.rahim.common.exception.BadRequestException;
import com.rahim.common.exception.DuplicateEntityException;
import com.rahim.common.exception.ServiceException;
import com.rahim.common.util.DateUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
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

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

  @Override
  @Transactional(rollbackFor = Exception.class)
  public RegisterResponse register(RegisterRequest registerRequest, HttpServletRequest request) {
    log.info("Starting registration process for user: {}", registerRequest.getUsername());

    validatePayload(registerRequest);

    log.debug("Checking if username {} already exists", registerRequest.getUsername());
    if (userRepository.existsByUsername(registerRequest.getUsername())) {
      log.warn("Registration failed: Username {} already exists", registerRequest.getUsername());
      throw new DuplicateEntityException(
          "User with username " + registerRequest.getUsername() + " already exists.");
    }

    log.debug("Checking if email {} already exists", registerRequest.getEmail());
    if (userRepository.existsByEmail(registerRequest.getEmail())) {
      log.warn("Registration failed: Email {} already exists", registerRequest.getEmail());
      throw new DuplicateEntityException(
          "User with email " + registerRequest.getEmail() + " already exists.");
    }

    log.debug("Creating new user entity for username: {}", registerRequest.getUsername());
    User user = createUser(registerRequest, request);
    log.info("User created successfully with ID: {}", user.getId());

    log.debug("Assigning USER role to user: {}", user.getId());
    roleService.assignRoleToUser(user, Role.USER);
    log.debug("Role USER assigned successfully to user: {}", user.getId());

    try {
      log.debug("Initiating email verification process for user: {}", user.getId());
      verificationService.sendEmailVerification(user);
      log.info("Email verification initiated successfully for user: {}", user.getId());
    } catch (Exception e) {
      log.error("Verification failed for user: {}. Error: {}", user.getId(), e.getMessage());
      throw new ServiceException("Failed to complete registration");
    }

    log.info("Registration completed successfully for user: {}", user.getId());
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

    if (email == null) {
      log.error("Email is required for verification");
      throw new BadRequestException("Email is required for verification");
    }

    if (verificationCode == null) {
      log.error("Verification code is required");
      throw new BadRequestException("Verification code is required");
    }

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new BadRequestException("User not found with email: " + email));

    if (user.isEmailVerified()) {
      log.warn("User with email {} is already verified", email);
      throw new BadRequestException("User with email " + email + " is already verified");
    }

    try {
      boolean matches = verificationService.verifyEmail(user.getId(), verificationCode);

      UserData userData =
          UserData.builder().username(user.getUsername()).email(user.getEmail()).build();

      if (matches) {
        log.info("Email verification successful for user with email: {}", email);

        user.setEmailVerified(true);
        user.setAccountLocked(false);
        user.setUpdatedAt(DateUtil.nowUtc());
        userRepository.save(user);

        userData.setVerifiedAt(DateUtil.formatOffsetDateTime(user.getUpdatedAt()));

        return VerificationResponse.builder()
            .status(ResponseStatus.SUCCESS)
            .message("Email verified successfully")
            .userData(userData)
            .build();
      } else {
        log.warn("Verification code does not match for user with email: {}", email);
        return VerificationResponse.builder()
            .status(ResponseStatus.INVALID)
            .message("Verification code does not match")
            .userData(userData)
            .build();
      }
    } catch (VerificationException e) {
      log.warn("Invalid verification for user with email {}: {}", email, e.getMessage());
      return VerificationResponse.builder()
          .status(ResponseStatus.INVALID)
          .message(e.getMessage())
          .build();
    } catch (Exception e) {
      log.error(
          "Error during email verification for user with email {}: {}", email, e.getMessage(), e);
      throw new ServiceException("Failed to verify email");
    }
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  private void validatePayload(RegisterRequest registerRequest) {
    log.debug(
        "Validating registration request payload for user: {}", registerRequest.getUsername());

    String email = registerRequest.getEmail();
    if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
      log.error("Invalid email format provided in registration request: {}", email);
      throw new BadRequestException("Invalid email format provided in registration request");
    }

    String username = registerRequest.getUsername();
    String password = registerRequest.getPassword();
    String firstName = registerRequest.getFirstName();
    String lastName = registerRequest.getLastName();
    String phoneNumber = registerRequest.getPhoneNumber();

    if (StringUtils.isAnyBlank(username, password, firstName, lastName)) {
      log.error("Invalid registration request payload: {}", registerRequest);
      throw new BadRequestException("Invalid registration request payload");
    }

    if (phoneNumber != null
        && !phoneNumber.isBlank()
        && userRepository.existsByPhoneNumber(phoneNumber)) {
      log.error("Phone number already exists: {}", phoneNumber);
      throw new BadRequestException("Phone number is already in use");
    }

    log.debug(
        "Registration request payload validated successfully for user: {}",
        registerRequest.getUsername());
  }

  private User createUser(RegisterRequest registerRequest, HttpServletRequest request) {
    User user =
        User.builder()
            .username(registerRequest.getUsername())
            .email(registerRequest.getEmail())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .firstName(registerRequest.getFirstName())
            .lastName(registerRequest.getLastName())
            .phoneNumber(registerRequest.getPhoneNumber())
            .emailVerified(false)
            .phoneVerified(false)
            .locale(RequestUtils.getClientLocale(request))
            .timezone(RequestUtils.getClientTimezone(request))
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .accountExpired(false)
            .accountLocked(true)
            .build();

    return userRepository.save(user);
  }
}
