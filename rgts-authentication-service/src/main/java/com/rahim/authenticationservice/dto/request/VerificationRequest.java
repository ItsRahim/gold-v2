package com.rahim.authenticationservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @created 11/06/2025
 * @author Rahim Ahmed
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {
  private String email;
  private String verificationCode;
}
