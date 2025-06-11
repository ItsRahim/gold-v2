package com.rahim.emailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(
    basePackages = {"com.rahim.emailservice", "com.rahim.kafkaservice", "com.rahim.common"})
public class EmailServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(EmailServiceApplication.class, args);
  }
}
