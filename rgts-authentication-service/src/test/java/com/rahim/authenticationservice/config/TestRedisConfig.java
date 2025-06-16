package com.rahim.authenticationservice.config;

import com.redis.testcontainers.RedisContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @created 26/05/2025
 * @author Rahim Ahmed
 */
@Configuration
@Profile("test")
public class TestRedisConfig {

  private final RedisContainer redisContainer;

  public TestRedisConfig(RedisContainer redisContainer) {
    this.redisContainer = redisContainer;
  }

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(
        redisContainer.getHost(), redisContainer.getFirstMappedPort());
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    return template;
  }
}
