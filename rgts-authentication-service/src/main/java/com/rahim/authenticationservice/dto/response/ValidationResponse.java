package com.rahim.authenticationservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @created 23/07/2025
 * @author Rahim Ahmed
 */
@Data
@AllArgsConstructor
public class ValidationResponse {
  private String userId;
  private String username;
  private List<String> roles;
}
