package com.rahim.authenticationservice.enums;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
public enum BlacklistReason {
  MANUAL_LOGOUT,
  TOKEN_EXPIRED,
  PASSWORD_RESET,
  SUSPICIOUS_ACTIVITY,
  COMPROMISED_CREDENTIALS,
  ACCOUNT_DELETION,
  USER_REQUESTED,
  UNKNOWN,
  ADMIN_ACTION
}
