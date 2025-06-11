package com.rahim.emailservice;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.rahim.emailservice.BaseTestContainerConfig.mailhog;

/**
 * @created 11/06/2025
 * @author Rahim Ahmed
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(BaseTestContainerConfig.class)
public class BaseTestConfiguration {

  @BeforeAll
  static void startContainer() {
    mailhog.start();
  }

  @DynamicPropertySource
  static void overrideMailProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.mail.host", mailhog::getHost);
    registry.add("spring.mail.port", () -> mailhog.getMappedPort(1025));
    registry.add("spring.mail.properties.mail.smtp.auth", () -> false);
    registry.add("spring.mail.properties.mail.smtp.starttls.enable", () -> false);
  }
}
