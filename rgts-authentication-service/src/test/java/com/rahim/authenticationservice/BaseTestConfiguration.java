package com.rahim.authenticationservice;

import static com.rahim.authenticationservice.BaseTestContainerConfig.postgres;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahim.cachemanager.service.RedisService;
import com.rahim.common.handler.ApiExceptionHandler;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * @created 26/05/2025
 * @author Rahim Ahmed
 */
@Transactional
@ActiveProfiles("test")
@Import({BaseTestContainerConfig.class, ApiExceptionHandler.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTestConfiguration {

  @Autowired private RedisService redisService;
  @Autowired private DataSource dataSource;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public String requestToJson(Object object) throws Exception {
    return objectMapper.writeValueAsString(object);
  }

  @BeforeAll
  static void startContainers() {
    postgres.start();
  }

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.flyway.url", postgres::getJdbcUrl);
    registry.add("spring.flyway.user", postgres::getUsername);
    registry.add("spring.flyway.password", postgres::getPassword);
  }

  @AfterEach
  void tearDown() {
    Flyway.configure().dataSource(dataSource).cleanDisabled(false).load().clean();
  }
}
