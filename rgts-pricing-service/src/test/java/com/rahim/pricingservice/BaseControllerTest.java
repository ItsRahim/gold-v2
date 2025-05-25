package com.rahim.pricingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahim.common.handler.ApiExceptionHandler;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
@Transactional
@ActiveProfiles("test")
@Import(ApiExceptionHandler.class)
@TestPropertySource("classpath:bootstrap.yml")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseControllerTest {
  private final ObjectMapper objectMapper = new ObjectMapper();

  public String requestToJson(Object object) throws Exception {
    return objectMapper.writeValueAsString(object);
  }
}
