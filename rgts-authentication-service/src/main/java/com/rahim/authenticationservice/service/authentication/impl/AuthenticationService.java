package com.rahim.authenticationservice.service.authentication.impl;

import com.rahim.authenticationservice.dto.enums.ResponseStatus;
import com.rahim.authenticationservice.dto.request.RegisterRequest;
import com.rahim.authenticationservice.dto.response.RegisterResponse;
import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.enums.Role;
import com.rahim.authenticationservice.repository.UserRepository;
import com.rahim.authenticationservice.service.authentication.IAuthenticationService;
import com.rahim.authenticationservice.service.role.IRoleService;
import com.rahim.authenticationservice.service.verification.IVerificationService;
import com.rahim.authenticationservice.util.RequestUtils;
import com.rahim.common.exception.BadRequestException;
import com.rahim.common.exception.DuplicateEntityException;
import com.rahim.common.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final PasswordEncoder passwordEncoder;

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
      log.error("Verification failed for user: {}. Error: {}", user.getId(), e.getMessage(), e);
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
