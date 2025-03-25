package com.rahim.proto.exception;

/**
 * @author Rahim Ahmed
 * @created 16/03/2025
 */
public class GrpcRequestException extends RuntimeException {

  public GrpcRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  public GrpcRequestException(String message) {
    super(message);
  }
}
