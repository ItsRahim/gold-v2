package com.rahim.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(
    basePackages = {
      "com.rahim.gatewayservice",
      "com.rahim.cachemanager",
    })
public class GatewayServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(GatewayServiceApplication.class, args);
  }
}
