package com.rahim.pricingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Rahim Ahmed
 * @created 16/03/2025
 */
@EnableCaching
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(
    basePackages = {
      "com.rahim.pricingservice",
      "com.rahim.kafkaservice",
      "com.rahim.common",
      "com.rahim.cachemanager"
    })
class PricingServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(PricingServiceApplication.class, args);
  }
}
