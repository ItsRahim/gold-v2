package com.rahim.emailservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.rahim.common.util.DateUtil;
import com.rahim.emailservice.BaseTestConfiguration;
import com.rahim.emailservice.dto.EmailVerificationData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @created 11/06/2025
 * @author Rahim Ahmed
 */
class EmailTemplateServiceTest extends BaseTestConfiguration {
  @Autowired private IEmailTemplateService emailTemplateService;

  @Test
  void shouldGenerateVerificationEmail_withAllFields() {
    String expiration = DateUtil.formatOffsetDateTime(DateUtil.nowUtc());

    EmailVerificationData data =
        EmailVerificationData.builder()
            .firstName("John")
            .lastName("Doe")
            .username("JDoe")
            .verificationCode("abc123")
            .verificationId("xyz987")
            .expirationTime(expiration)
            .build();

    String result = emailTemplateService.generateVerificationEmail(data);

    assertThat(result)
        .contains("John")
        .contains("Doe")
        .contains("JDoe")
        .contains("abc123")
        .contains("xyz987")
        .contains(expiration);
  }
}
