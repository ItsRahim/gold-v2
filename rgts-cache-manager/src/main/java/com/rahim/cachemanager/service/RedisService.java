package com.rahim.cachemanager.service;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

/**
 * @created 04/05/2025
 * @author Rahim Ahmed
 */
@Service
public class RedisService {

  private final RedisTemplate<String, Object> redisTemplate;

  @Autowired
  public RedisService(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void setValue(String key, Object value) {
    redisTemplate.opsForValue().set(key, value);
  }

  public void setValue(String key, Object value, long timeoutInSeconds) {
    redisTemplate.opsForValue().set(key, value, timeoutInSeconds, TimeUnit.SECONDS);
  }

  public Object getValue(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public void putHash(String key, String hashKey, Object value) {
    redisTemplate.opsForHash().put(key, hashKey, value);
  }

  public Object getHash(String key, String hashKey) {
    return redisTemplate.opsForHash().get(key, hashKey);
  }

  public Map<Object, Object> getAllHash(String key) {
    return redisTemplate.opsForHash().entries(key);
  }

  public void addToSet(String key, Object... values) {
    redisTemplate.opsForSet().add(key, values);
  }

  public Set<Object> getSetMembers(String key) {
    return redisTemplate.opsForSet().members(key);
  }

  public boolean hasKey(String key) {
    return redisTemplate.hasKey(key);
  }

  public void expire(String key, long timeoutInSeconds) {
    redisTemplate.expire(key, Duration.ofSeconds(timeoutInSeconds));
  }

  public void deleteKey(String key) {
    redisTemplate.delete(key);
  }
}
