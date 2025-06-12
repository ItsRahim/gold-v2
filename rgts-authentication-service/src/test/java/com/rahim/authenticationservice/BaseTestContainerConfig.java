package com.rahim.authenticationservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * @created 26/05/2025
 * @author Rahim Ahmed
 */
@TestConfiguration
public abstract class BaseTestContainerConfig {

  @SuppressWarnings("resource")
  static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
          .withDatabaseName("rgts-test-db");
}
