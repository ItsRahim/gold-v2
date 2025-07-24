package com.rahim.authenticationservice.controller;

import static com.rahim.authenticationservice.constants.Endpoints.*;

import com.rahim.authenticationservice.dto.request.AuthRequest;
import com.rahim.authenticationservice.dto.request.RegisterRequest;
import com.rahim.authenticationservice.dto.request.VerificationRequest;
import com.rahim.authenticationservice.dto.response.AuthResponse;
import com.rahim.authenticationservice.dto.response.RegisterResponse;
import com.rahim.authenticationservice.dto.response.ValidationResponse;
import com.rahim.authenticationservice.dto.response.VerificationResponse;
import com.rahim.authenticationservice.service.authentication.IAuthenticationService;
import com.rahim.authenticationservice.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH_SERVICE)
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthenticationController {
  private final IAuthenticationService authenticationService;

  @Operation(
      summary = "Register a new user",
      description = "Creates a new user account and sends verification email")
  @ApiResponse(responseCode = "201", description = "User registered successfully")
  @ApiResponse(responseCode = "400", description = "Invalid request data")
  @ApiResponse(responseCode = "409", description = "User already exists")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  @PostMapping(REGISTER)
  public ResponseEntity<RegisterResponse> registerUser(
      @RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
    RegisterResponse response = authenticationService.register(registerRequest, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(
      summary = "Verify user email via request body",
      description =
          "Verifies the user's email address using a verification code and email in the request body.")
  @ApiResponse(responseCode = "200", description = "Email verified successfully")
  @ApiResponse(responseCode = "400", description = "Invalid verification code or request data")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  @PostMapping(VERIFY_EMAIL)
  public ResponseEntity<VerificationResponse> verifyEmailWithBody(
      @RequestBody VerificationRequest verificationRequest, HttpServletRequest request) {
    VerificationResponse verificationResponse =
        authenticationService.verifyEmail(verificationRequest, request);
    return ResponseEntity.status(HttpStatus.OK).body(verificationResponse);
  }

  @Operation(
      summary = "Verify user email via secure link",
      description =
          "Verifies the user's email address using a secure hashed token provided via a verification link.")
  @ApiResponse(responseCode = "200", description = "Email verified successfully")
  @ApiResponse(responseCode = "400", description = "Invalid or expired verification token")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  @GetMapping(VERIFY_EMAIL)
  public ResponseEntity<VerificationResponse> verifyEmailWithLink(
      @RequestParam("token") String verificationCode,
      @RequestParam("id") UUID verificationId,
      HttpServletRequest request) {
    VerificationResponse verificationResponse =
        authenticationService.verifyEmail(verificationCode, verificationId, request);
    return ResponseEntity.status(HttpStatus.OK).body(verificationResponse);
  }

  @Operation(
      summary = "Login a user",
      description = "Authenticates a user and returns an access token")
  @ApiResponse(responseCode = "200", description = "User logged in successfully")
  @ApiResponse(responseCode = "400", description = "Invalid login credentials")
  @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials")
  @PostMapping(LOGIN)
  public ResponseEntity<AuthResponse> loginUser(
      @RequestBody AuthRequest authRequest, HttpServletRequest request) {
    AuthResponse accessToken = authenticationService.login(authRequest, request);
    return ResponseEntity.status(HttpStatus.OK).body(accessToken);
  }

  @Operation(
      summary = "Validate a JWT token",
      description =
          "Validates a JWT token and returns user details such as userId, username, and roles")
  @ApiResponse(responseCode = "200", description = "Token is valid and user data is returned")
  @ApiResponse(responseCode = "401", description = "Unauthorized - token is missing or invalid")
  @ApiResponse(responseCode = "400", description = "Bad Request - malformed token")
  @PostMapping(VALIDATE_TOKEN)
  public ResponseEntity<ValidationResponse> validateToken(
      @RequestHeader("Authorization") String authHeader) {
    ValidationResponse validationResponse = authenticationService.validateToken(authHeader);
    return ResponseEntity.status(HttpStatus.OK).body(validationResponse);
  }
}
