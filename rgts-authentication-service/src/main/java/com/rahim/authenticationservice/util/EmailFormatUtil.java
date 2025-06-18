package com.rahim.authenticationservice.util;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * @created 11/06/2025
 * @author Rahim Ahmed
 */
public class EmailFormatUtil {
  private EmailFormatUtil() {}

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

  public static boolean isInvalidEmail(String email) {
    return StringUtils.isEmpty(email) || !EMAIL_PATTERN.matcher(email).matches();
  }
}
