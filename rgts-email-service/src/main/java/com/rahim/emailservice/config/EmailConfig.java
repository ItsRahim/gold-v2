package com.rahim.emailservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author Rahim Ahmed
 * @created 10/06/2025
 */
@Configuration
public class EmailConfig {

  @Value("${spring.mail.host}")
  private String host;

  @Value("${spring.mail.port}")
  private int port;

  @Value("${spring.mail.username}")
  private String username;

  @Value("${spring.mail.password}")
  private String password;

  @Value("${spring.mail.properties.mail.smtp.auth}")
  private String smtpAuth;

  @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
  private String startTlsEnable;

  @Bean
  public JavaMailSender javaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);
    mailSender.setUsername(username);
    mailSender.setPassword(password);

    mailSender.getJavaMailProperties().put("mail.smtp.auth", smtpAuth);
    mailSender.getJavaMailProperties().put("mail.smtp.starttls.enable", startTlsEnable);

    return mailSender;
  }
}
