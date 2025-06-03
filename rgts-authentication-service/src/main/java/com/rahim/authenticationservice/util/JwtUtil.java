package com.rahim.authenticationservice.util;

import com.rahim.common.util.DateUtil;

import java.time.Instant;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
public class JwtUtil {
    public static String calculateExpiryTime(long expiryInMilliSeconds) {
        return DateUtil.formatInstant(Instant.now().plusMillis(expiryInMilliSeconds));
    }
}
