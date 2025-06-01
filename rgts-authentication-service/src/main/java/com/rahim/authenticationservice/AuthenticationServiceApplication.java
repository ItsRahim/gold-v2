package com.rahim.authenticationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthenticationServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(AuthenticationServiceApplication.class, args);
  }
}
