package com.rahim.authenticationservice.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.rahim.authenticationservice.BaseTestConfiguration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @created 12/06/2025
 * @author Rahim Ahmed
 */
class EmailFormatUtilTest extends BaseTestConfiguration {
  @Autowired private EmailFormatUtil emailFormatUtil;

  @ParameterizedTest
  @CsvSource({
    "rahim@gmail.com",
    "john.doe@yahoo.com",
    "mary-jane@hotmail.com",
    "some.valid.email@live.com"
  })
  void isValidEmail_ValidEmails_ReturnsTrue(String email) {
    assertThat(emailFormatUtil.isInvalidEmail(email)).isFalse();
  }

  @ParameterizedTest
  @CsvSource({
    "plainaddress",
    "@missingusername.com",
    "username@.com",
    "username@com",
    "username@domain..com",
  })
  void isInvalidEmail_InvalidEmails_ReturnsTrue(String email) {
    assertThat(emailFormatUtil.isInvalidEmail(email)).isTrue();
  }
}
