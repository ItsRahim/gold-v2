package com.rahim.authenticationservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@AllArgsConstructor
@NoArgsConstructor
public class UserData {
  private String username;
  private String email;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String verifiedAt;
}
