package com.rahim.authenticationservice.dto.internal;

import com.rahim.authenticationservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@NoArgsConstructor
@AllArgsConstructor
public class AuthResult {
  public User user;
  public String refreshToken;
}
