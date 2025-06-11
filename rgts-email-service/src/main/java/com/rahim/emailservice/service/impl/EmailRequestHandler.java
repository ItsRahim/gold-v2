package com.rahim.emailservice.service.impl;

import com.rahim.emailservice.constants.EmailSubjects;
import com.rahim.emailservice.dto.EmailVerificationData;
import com.rahim.emailservice.exception.EmailProcessingException;
import com.rahim.emailservice.service.IEmailTemplateService;
import com.rahim.emailservice.service.IEmailRequestHandler;
import com.rahim.emailservice.service.IEmailSenderService;
import com.rahim.proto.protobuf.email.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @created 10/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailRequestHandler implements IEmailRequestHandler {
  private final IEmailTemplateService emailGenerator;
  private final IEmailSenderService emailSenderService;

  @Override
  public void handleEmailRequest(EmailRequest emailRequest) {
    if (emailRequest == null) {
      log.warn("Received a null email request");
      throw new EmailProcessingException("Email request cannot be null");
    }

    switch (emailRequest.getTemplate()) {
      case VERIFICATION_REQUEST -> {
        log.info("Doing something here for verification request");
        sendVerificationEmail(emailRequest);
      }
      case UNKNOWN -> {
        log.warn("Received an unknown email request type: {}", emailRequest.getTemplate());
        throw new EmailProcessingException(
            "Unknown email request type: " + emailRequest.getTemplate());
      }
      default -> {
        log.error("Unhandled email request type: {}", emailRequest.getTemplate());
        throw new EmailProcessingException(
            "Unhandled email request type: " + emailRequest.getTemplate());
      }
    }
  }

  private void sendVerificationEmail(EmailRequest emailRequest) {
    try {
      String verificationCode = emailRequest.getVerificationData().getVerificationCode();
      String expirationTime = emailRequest.getVerificationData().getExpirationTime();
      String firstName = emailRequest.getFirstName();
      String lastName = emailRequest.getLastName();
      String username = emailRequest.getUsername();
      String recipientEmail = emailRequest.getRecipientEmail();

      if (StringUtils.isAnyBlank(
          verificationCode, expirationTime, firstName, lastName, username, recipientEmail)) {
        log.error("Missing required fields in email request: {}", emailRequest);
        throw new EmailProcessingException("Missing required fields in email request");
      }

      EmailVerificationData emailVerificationData =
          EmailVerificationData.builder()
              .firstName(firstName)
              .lastName(lastName)
              .username(username)
              .verificationCode(verificationCode)
              .expirationTime(expirationTime)
              .build();

      String emailContent = emailGenerator.generateVerificationEmail(emailVerificationData);
      emailSenderService.sendEmail(recipientEmail, EmailSubjects.EMAIL_VERIFICATION, emailContent);
      log.info("Verification email sent to: {}", recipientEmail);
    } catch (Exception e) {
      log.error("Failed to send verification email to", e);
    }
  }
}
