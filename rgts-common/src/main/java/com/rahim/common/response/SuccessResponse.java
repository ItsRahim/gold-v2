package com.rahim.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @created 18/04/2025
 * @author Rahim Ahmed
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse<T> {
  private String message;
  private T data;

  public static <T> SuccessResponse<T> of(String message, T data) {
    return SuccessResponse.<T>builder()
            .message(message)
            .data(data)
            .build();
  }
}
