package com.rahim.authenticationservice.controller;

import com.rahim.authenticationservice.dto.request.LoginUserRequest;
import com.rahim.authenticationservice.dto.request.SignupUserRequest;
import com.rahim.authenticationservice.dto.request.VerifyUserRequest;
import com.rahim.authenticationservice.dto.response.LoginResponse;
import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.service.AuthenticationService;
import com.rahim.authenticationservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auth")
public class AuthenticationController {
  private final JwtService jwtService;
  private final AuthenticationService authenticationService;

  @PostMapping("/signup")
  public ResponseEntity<User> signup(@RequestBody SignupUserRequest request) {
    User signedupUser = authenticationService.signup(request);
    return ResponseEntity.status(HttpStatus.OK).body(signedupUser);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginUserRequest request) {
    User loggedInUser = authenticationService.login(request);
    String jwtToken = jwtService.generateToken(loggedInUser);
    LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
    return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
  }

  @PostMapping("/verify")
  public ResponseEntity<String> verifyUser(@RequestBody VerifyUserRequest request) {
    try {
      authenticationService.verifyUser(request);
      return ResponseEntity.status(HttpStatus.OK).body("User verified successfully");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @PostMapping("/resend")
  public ResponseEntity<String> resendVerificationCode(@RequestBody String username) {
    try {
      authenticationService.resendVerificationEmail(username);
      return ResponseEntity.status(HttpStatus.OK).body("Verification code resent successfully");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }
}
