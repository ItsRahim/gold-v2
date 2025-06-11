package com.rahim.emailservice.service;

import com.rahim.emailservice.BaseTestConfiguration;
import com.rahim.emailservice.service.impl.EmailSenderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import static com.rahim.emailservice.BaseTestContainerConfig.getMailHogHttpEndpoint;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class EmailSenderServiceTest extends BaseTestConfiguration {

  @Autowired private EmailSenderService emailSenderService;
  private final RestTemplate restTemplate = new RestTemplate();

  @AfterEach
  void clearMailHogInbox() {
    String url = getMailHogHttpEndpoint() + "/api/v1/messages";
    restTemplate.delete(url);
  }

  @Test
  void shouldSendEmailSuccessfully() {
    emailSenderService.sendEmail(
        "recipient@example.com", "Test Subject", "<h1>Hello from MailHog Test</h1>");

    String url = getMailHogHttpEndpoint() + "/api/v2/messages";

    String response = restTemplate.getForObject(url, String.class);

    assertThat(response).contains("Test Subject");
    assertThat(response).contains("recipient@example.com");
    assertThat(response).contains("Hello from MailHog Test");
  }
}
