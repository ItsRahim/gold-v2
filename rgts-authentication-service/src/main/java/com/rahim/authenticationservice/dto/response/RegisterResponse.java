package com.rahim.authenticationservice.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @created 09/06/2025
 * @author Rahim Ahmed
 */
@Data
@SuperBuilder
public class RegisterResponse {

  @JsonCreator
  public RegisterResponse(
      @JsonProperty("id") UUID id,
      @JsonProperty("username") String username,
      @JsonProperty("email") String email) {
    this.id = id;
    this.username = username;
    this.email = email;
  }

  private UUID id;
  private String username;
  private String email;
}
