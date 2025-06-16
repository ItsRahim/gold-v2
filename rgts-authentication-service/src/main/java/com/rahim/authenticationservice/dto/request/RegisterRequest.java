package com.rahim.authenticationservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
  private String email;
  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private String phoneNumber;
}
