package com.rahim.jwtcore.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @created 26/07/2025
 * @author Rahim Ahmed
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenVerificationResponse {
  private String userId;
  private String username;
  private List<String> roles;
  private Date expiry;
}
