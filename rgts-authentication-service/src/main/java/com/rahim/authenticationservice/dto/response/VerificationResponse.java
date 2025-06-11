package com.rahim.authenticationservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @created 11/06/2025
 * @author Rahim Ahmed
 */
@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationResponse extends BaseResponse {
  private UserData userData;
}
