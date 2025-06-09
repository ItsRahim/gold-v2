package com.rahim.authenticationservice.enums;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
public enum LoginFailureReason {
  WRONG_PASSWORD,
  ACCOUNT_LOCKED,
  USER_NOT_FOUND,
  TOKEN_EXPIRED,
  MFA_REQUIRED,
  MFA_FAILED,
  UNKNOWN
}
