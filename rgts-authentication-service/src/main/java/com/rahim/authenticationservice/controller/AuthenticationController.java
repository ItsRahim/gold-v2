package com.rahim.authenticationservice.controller;

import static com.rahim.authenticationservice.constants.Endpoints.*;

import com.rahim.authenticationservice.dto.request.RegisterRequest;
import com.rahim.authenticationservice.dto.response.RegisterResponse;
import com.rahim.authenticationservice.service.authentication.IAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "User registered successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request data"),
    @ApiResponse(responseCode = "409", description = "User already exists"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping(REGISTER)
  public ResponseEntity<RegisterResponse> register(
      @RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
    RegisterResponse response = authenticationService.register(registerRequest, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
