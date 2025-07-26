package com.rahim.authenticationservice.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
@Data
@AllArgsConstructor
public class AuthResponse {
  private String accessToken;
  private String username;
  private String userId;
  private String firstName;
  private String lastName;
  private List<String> roles;
}
