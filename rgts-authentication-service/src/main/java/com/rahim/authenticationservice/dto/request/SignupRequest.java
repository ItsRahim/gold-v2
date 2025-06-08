package com.rahim.authenticationservice.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
@Getter
@Setter
public class SignupRequest {
  String username;
  String email;
  String password;
  String firstName;
  String lastName;
  String phoneNumber;
}
