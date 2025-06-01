package com.rahim.authenticationservice.controller;

import com.rahim.authenticationservice.dto.internal.AuthResult;
import com.rahim.authenticationservice.dto.request.LoginUserRequest;
import com.rahim.authenticationservice.dto.request.RefreshTokenRequest;
import com.rahim.authenticationservice.dto.request.ResendVerificationRequest;
import com.rahim.authenticationservice.dto.request.SignupUserRequest;
import com.rahim.authenticationservice.dto.request.VerifyUserRequest;
import com.rahim.authenticationservice.dto.response.LoginResponse;
import com.rahim.authenticationservice.dto.response.SignupResponse;
import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.service.AuthenticationService;
import com.rahim.authenticationservice.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.rahim.authenticationservice.constants.Endpoints.*;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH_SERVICE)
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthenticationController {

  private final JwtService jwtService;
  private final AuthenticationService authenticationService;

  @Operation(
      summary = "Register new user",
      description = "Creates a new user account and sends verification email")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "User registered successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request data"),
    @ApiResponse(responseCode = "409", description = "User already exists"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping(SIGNUP)
  public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupUserRequest request) {
    log.info("User signup attempt for username: {}", request.getUsername());

    User signedUpUser = authenticationService.signup(request);
    SignupResponse response =
        SignupResponse.builder()
            .id(signedUpUser.getId())
            .username(signedUpUser.getUsername())
            .message(
                "User registered successfully. Please check your email to verify your account.")
            .build();

    log.info("User signed up successfully with ID: {}", signedUpUser.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(
      summary = "Verify user email",
      description = "Verifies user email using the verification code sent via email")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "User verified successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid verification code"),
    @ApiResponse(responseCode = "410", description = "Verification code expired"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping(VERIFY)
  public ResponseEntity<String> verifyUser(@Valid @RequestBody VerifyUserRequest request) {
    log.info("User verification attempt for username: {}", request.getUsername());

    authenticationService.verifyUser(request);

    log.info("User verified successfully: {}", request.getUsername());
    return ResponseEntity.status(HttpStatus.OK).body("User verified successfully.");
  }

  @Operation(
      summary = "User login",
      description = "Authenticates user and returns access and refresh tokens")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Login successful"),
    @ApiResponse(responseCode = "401", description = "Invalid credentials"),
    @ApiResponse(responseCode = "403", description = "User not verified"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping(LOGIN)
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginUserRequest request) {
    log.info("Login attempt for username: {}", request.getUsername());

    AuthResult authResult = authenticationService.login(request);
    User authenticatedUser = authResult.user;
    String refreshToken = authResult.refreshToken;
    String accessToken = jwtService.generateToken(authenticatedUser);

    LoginResponse loginResponse =
        LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(jwtService.getExpirationTime())
            .tokenType("Bearer")
            .user(
                SignupResponse.builder()
                    .id(authenticatedUser.getId())
                    .username(authenticatedUser.getUsername())
                    .build())
            .build();

    log.info("User logged in successfully: {}", authenticatedUser.getUsername());
    return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
  }

  @Operation(
      summary = "Refresh access token",
      description = "Generates new access token using valid refresh token")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping(REFRESH_TOKEN)
  public ResponseEntity<Object> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
    log.info("Token refresh attempt");

    try {
      String refreshToken = request.getRefreshToken();
      String username = jwtService.extractUsername(refreshToken);
      User user = authenticationService.findByUsername(username);

      if (jwtService.isValidRefreshToken(refreshToken, user)) {
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        LoginResponse response =
            LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtService.getExpirationTime())
                .tokenType("Bearer")
                .build();

        log.info("Token refreshed successfully for user: {}", username);
        return ResponseEntity.status(HttpStatus.OK).body(response);
      }

      log.warn("Invalid refresh token attempt for user: {}", username);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Invalid or expired refresh token");

    } catch (Exception e) {
      log.error("Token refresh failed: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }
  }

  @Operation(
      summary = "Resend verification email",
      description = "Sends a new verification code to user's email")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Verification email sent"),
    @ApiResponse(responseCode = "400", description = "User not found or already verified"),
    @ApiResponse(responseCode = "429", description = "Too many requests"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping(RESEND)
  public ResponseEntity<String> resendVerificationCode(
      @Valid @RequestBody ResendVerificationRequest request) {
    log.info("Resend verification code request for username: {}", request.getUsername());

    authenticationService.resendVerificationEmail(request.getUsername());

    log.info("Verification code resent successfully to: {}", request.getUsername());
    return ResponseEntity.status(HttpStatus.OK)
        .body("Verification code sent successfully. Please check your email.");
  }

  @Operation(summary = "User logout", description = "Invalidates user's refresh token")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Logout successful"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping(LOGOUT)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest request) {
    log.info("Logout attempt");

    try {
      authenticationService.logout(request.getRefreshToken());
      log.info("User logged out successfully");
      return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
    } catch (Exception e) {
      log.error("Logout failed: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Logout failed: " + e.getMessage());
    }
  }

  @Operation(
      summary = "Get token info",
      description = "Returns information about the current access token")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Token info retrieved successfully"),
    @ApiResponse(responseCode = "401", description = "Invalid token"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping(TOKEN_INFO)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Object> getTokenInfo(@RequestHeader("Authorization") String authHeader) {
    try {
      String token = authHeader.replace("Bearer ", "");
      String username = jwtService.extractUsername(token);
      long timeUntilExpiration = jwtService.getTimeUntilExpiration(token);
      String tokenType = jwtService.extractTokenType(token);

      return ResponseEntity.ok(
          Map.of(
              "username", username,
              "tokenType", tokenType,
              "timeUntilExpirationMs", timeUntilExpiration,
              "isExpired", jwtService.isTokenExpired(token)));
    } catch (Exception e) {
      log.error("Failed to get token info: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
          "error", "Invalid token",
          "message", e.getMessage()
      ));
    }
  }
}
