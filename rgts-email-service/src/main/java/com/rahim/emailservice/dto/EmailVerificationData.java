package com.rahim.emailservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @created 10/06/2025
 * @author Rahim Ahmed
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationData extends BaseEmail {
  private String verificationCode;
  private String verificationId;
  private String expirationTime;
}
