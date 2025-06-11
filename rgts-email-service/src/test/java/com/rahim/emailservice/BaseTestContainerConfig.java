package com.rahim.emailservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.GenericContainer;

/**
 * @created 11/06/2025
 * @author Rahim Ahmed
 */
@TestConfiguration
public abstract class BaseTestContainerConfig {

  @SuppressWarnings("resource")
  static final GenericContainer<?> mailhog =
      new GenericContainer<>("mailhog/mailhog").withExposedPorts(1025, 8025);

  public static String getMailHogHttpEndpoint() {
    return "http://" + mailhog.getHost() + ":" + mailhog.getMappedPort(8025);
  }
}
