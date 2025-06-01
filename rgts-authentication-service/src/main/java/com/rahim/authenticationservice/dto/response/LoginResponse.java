package com.rahim.authenticationservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
  private String token;
  private long expiresAt;
}
