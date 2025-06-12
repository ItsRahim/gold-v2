package com.rahim.emailservice.service.impl;

import com.rahim.emailservice.dto.EmailVerificationData;
import com.rahim.emailservice.service.IEmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @created 10/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateService implements IEmailTemplateService {
  private final TemplateEngine templateEngine;

  private static final String VERIFICATION_EMAIL_TEMPLATE = "user/verification-email";

  @Override
  public String generateVerificationEmail(
      EmailVerificationData emailVerificationData) {
    Context context = new Context();

    context.setVariable("firstName", emailVerificationData.getFirstName());
    context.setVariable("lastName", emailVerificationData.getLastName());
    context.setVariable("username", emailVerificationData.getUsername());
    context.setVariable("rawVerificationCode", emailVerificationData.getRawVerificationCode());
    context.setVariable("hashedVerificationCode", emailVerificationData.getHashedVerificationCode());
    context.setVariable("expirationTime", emailVerificationData.getExpirationTime());
    context.setVariable(
        "recipientName",
        emailVerificationData.getFirstName() + " " + emailVerificationData.getLastName());

    return templateEngine.process(VERIFICATION_EMAIL_TEMPLATE, context);
  }
}
