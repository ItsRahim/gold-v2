package com.rahim.pricingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahim.cachemanager.service.RedisService;
import com.rahim.common.handler.ApiExceptionHandler;
import com.rahim.pricingservice.entity.GoldPrice;
import com.rahim.pricingservice.entity.GoldPurity;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
@Transactional
@ActiveProfiles("test")
@Import(ApiExceptionHandler.class)
@TestPropertySource("classpath:application.yml")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseControllerTest {
  @Mock RedisService redisService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    GoldPurity goldPurity = new GoldPurity(1, "22K", 22, 24, false);
    when(redisService.getValue(anyString()))
        .thenReturn(new GoldPrice(1, goldPurity, BigDecimal.valueOf(100), Instant.now()));
  }

  public String requestToJson(Object object) throws Exception {
    return objectMapper.writeValueAsString(object);
  }
}
