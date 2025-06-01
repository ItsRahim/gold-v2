package com.rahim.authenticationservice.dto.request;

import lombok.Data;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Data
public class VerifyUserRequest {
  private String username;
  private String verificationCode;
}
