package com.rahim.pricingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahim.cachemanager.service.RedisService;
import com.rahim.pricingservice.entity.GoldPrice;
import com.rahim.pricingservice.entity.GoldPurity;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
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
@SpringBootTest
@TestPropertySource("classpath:application.yml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class BaseUnitTest {
  @Mock RedisService redisService;

  @BeforeEach
  void setUp() {
    GoldPurity goldPurity = new GoldPurity(1, "22K", 22, 24, false);
    when(redisService.getValue(anyString()))
        .thenReturn(new GoldPrice(1, goldPurity, BigDecimal.valueOf(100), Instant.now()));
  }
}
