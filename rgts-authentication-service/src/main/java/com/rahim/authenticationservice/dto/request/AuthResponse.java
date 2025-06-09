package com.rahim.authenticationservice.dto.request;

import lombok.Data;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
@Data
public class AuthResponse {
  private String username;
  private String accessToken;
  private String refreshToken;
}
