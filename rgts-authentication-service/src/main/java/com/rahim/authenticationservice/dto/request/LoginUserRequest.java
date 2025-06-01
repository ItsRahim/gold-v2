package com.rahim.authenticationservice.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Getter
@Setter
public class LoginUserRequest {
  private String username;
  private String password;
}
