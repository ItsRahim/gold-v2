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
import com.rahim.common.exception.DuplicateEntityException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * @created 09/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AuthenticationService implements IAuthenticationService {
  private final UserRepository userRepository;
  private final IRoleService roleService;
  private final IVerificationService verificationService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public RegisterResponse register(RegisterRequest registerRequest, HttpServletRequest request) {
    if (userRepository.existsByEmail(registerRequest.getEmail())) {
      throw new DuplicateEntityException(
          "User with email " + registerRequest.getEmail() + " already exists.");
    }

    if (userRepository.existsByUsername(registerRequest.getUsername())) {
      throw new DuplicateEntityException(
          "User with username " + registerRequest.getUsername() + " already exists.");
    }

    User user = createUser(registerRequest, request);
    roleService.assignRoleToUser(user, Role.USER);
    verificationService.sendEmailVerification(user);
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
