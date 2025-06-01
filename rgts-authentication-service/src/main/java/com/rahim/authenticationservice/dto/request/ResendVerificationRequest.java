package com.rahim.authenticationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResendVerificationRequest {

  @NotBlank(message = "Username is required")
  private String username;
}
