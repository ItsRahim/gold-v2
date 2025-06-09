package com.rahim.authenticationservice.dto.request;

import lombok.Data;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
@Data
public class RegisterRequest {
  String username;
  String email;
  String password;
  String firstName;
  String lastName;
  String phoneNumber;
}
