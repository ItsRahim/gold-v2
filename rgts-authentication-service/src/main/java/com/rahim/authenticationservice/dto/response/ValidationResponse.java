package com.rahim.authenticationservice.dto.response;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

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
  private Date expiry;
}
