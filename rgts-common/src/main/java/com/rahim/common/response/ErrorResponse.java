package com.rahim.common.response;

import com.rahim.common.util.DateUtil;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Getter
public class ErrorResponse {
  private final int status;
  private final String timestamp;
  private final String message;

  public ErrorResponse(String message, HttpStatus status) {
    this.status = status.value();
    this.timestamp = DateUtil.nowUtcFormatted();
    this.message = message;
  }
}
