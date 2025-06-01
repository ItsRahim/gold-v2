package com.rahim.authenticationservice.service;

import com.rahim.authenticationservice.dto.request.LoginUserRequest;
import com.rahim.authenticationservice.dto.request.SignupUserRequest;
import com.rahim.authenticationservice.dto.request.VerifyUserRequest;
import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.repository.UserRepository;
import com.rahim.kafkaservice.service.IKafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final IKafkaService kafkaService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  public User signup(SignupUserRequest signupUserRequest) {
    User user =
        User.builder()
            .username(signupUserRequest.getUsername())
            .email(signupUserRequest.getEmail())
            .password(passwordEncoder.encode(signupUserRequest.getPassword()))
            .build();

    user.setVerificationCode(generateVerificationCode());
    user.setVerificationExpiry(Instant.now().plusSeconds(900));
    user.setEnabled(false);
    sendVerificationEmail(user);
    return userRepository.save(user);
  }

  public User login(LoginUserRequest loginUserRequest) {
    User user =
        userRepository
            .findByUsername(loginUserRequest.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (!user.isEnabled()) {
      throw new RuntimeException("User account is not verified");
    }

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginUserRequest.getUsername(), loginUserRequest.getPassword()));

    return user;
  }

  public void verifyUser(VerifyUserRequest verifyUserRequest) {
    Optional<User> userOptional = userRepository.findByUsername(verifyUserRequest.getUsername());

    if (userOptional.isPresent()) {
      User user = userOptional.get();
      if (user.getVerificationExpiry().isBefore(Instant.now())) {
        throw new RuntimeException("Verification code has expired");
      }

      if (user.getVerificationCode().equals(verifyUserRequest.getVerificationCode())) {
        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationExpiry(null);
        userRepository.save(user);
      } else {
        throw new RuntimeException("Invalid verification code");
      }
    } else {
      throw new RuntimeException("User not found");
    }
  }

  public void resendVerificationEmail(String username) {
    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      if (user.isEnabled()) {
        throw new RuntimeException("User account is already verified");
      }
      user.setVerificationCode(generateVerificationCode());
      user.setVerificationExpiry(Instant.now().plusSeconds(900));
      sendVerificationEmail(user);
      userRepository.save(user);
    } else {
      throw new RuntimeException("User not found");
    }
  }

  private String generateVerificationCode() {
    return String.format("%06d", (int) (Math.random() * 1000000));
  }

  private void sendVerificationEmail(User user) {
    System.out.println(user);
  }
}
