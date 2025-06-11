package com.rahim.authenticationservice.dto.request;

import lombok.Data;

/**
 * @created 11/06/2025
 * @author Rahim Ahmed
 */
@Data
public class VerificationRequest {
  String email;
  String verificationCode;
}
