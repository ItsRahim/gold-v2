package com.rahim.authenticationservice.dto.request;

import lombok.Builder;
import lombok.Data;

/**
 * @created 11/06/2025
 * @author Rahim Ahmed
 */
@Data
@Builder
public class VerificationRequest {
  private String email;
  private String verificationCode;
}
