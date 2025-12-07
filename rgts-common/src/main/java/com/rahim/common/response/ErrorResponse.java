package com.rahim.common.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rahim.common.util.DateUtil;
import org.springframework.http.HttpStatus;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
public record ErrorResponse(int status, String timestamp, String message) {

  @JsonCreator
  public ErrorResponse(
      @JsonProperty("status") int status,
      @JsonProperty("timestamp") String timestamp,
      @JsonProperty("message") String message) {
    this.status = status;
    this.timestamp = timestamp;
    this.message = message;
  }

  public ErrorResponse(String message, HttpStatus status) {
    this(status.value(), DateUtil.nowUtcFormatted(), message);
  }
}
