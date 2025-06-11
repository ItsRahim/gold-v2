package com.rahim.emailservice.service.impl;

import com.rahim.emailservice.constants.EmailSubjects;
import com.rahim.emailservice.dto.EmailVerificationData;
import com.rahim.emailservice.exception.EmailProcessingException;
import com.rahim.emailservice.service.IEmailRequestHandler;
import com.rahim.emailservice.service.IEmailSenderService;
import com.rahim.emailservice.service.IEmailTemplateService;
import com.rahim.proto.protobuf.email.EmailRequest;
import java.util.ArrayList;
import java.util.List;
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
        log.debug("Generating verification email for: {}", emailRequest.getRecipientEmail());
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
      List<String> missingFields = new ArrayList<>();

      String verificationCode = emailRequest.getVerificationData().getVerificationCode();
      String expirationTime = emailRequest.getVerificationData().getExpirationTime();
      String firstName = emailRequest.getFirstName();
      String lastName = emailRequest.getLastName();
      String username = emailRequest.getUsername();
      String recipientEmail = emailRequest.getRecipientEmail();

      if (StringUtils.isBlank(verificationCode)) {
        log.error("Missing field: verificationCode");
        missingFields.add("verificationCode");
      }

      if (StringUtils.isBlank(expirationTime)) {
        log.error("Missing field: expirationTime");
        missingFields.add("expirationTime");
      }

      if (StringUtils.isBlank(firstName)) {
        log.error("Missing field: firstName");
        missingFields.add("firstName");
      }

      if (StringUtils.isBlank(lastName)) {
        log.error("Missing field: lastName");
        missingFields.add("lastName");
      }

      if (StringUtils.isBlank(username)) {
        log.error("Missing field: username");
        missingFields.add("username");
      }

      if (StringUtils.isBlank(recipientEmail)) {
        log.error("Missing field: recipientEmail");
        missingFields.add("recipientEmail");
      }

      if (!missingFields.isEmpty()) {
        String errorMessage =
            "Missing required fields in email request: " + String.join(", ", missingFields);
        throw new EmailProcessingException(errorMessage);
      }

      EmailVerificationData emailVerificationData =
          EmailVerificationData.builder()
              .firstName(firstName)
              .lastName(lastName)
              .username(username)
              .verificationCode(verificationCode)
              .expirationTime(expirationTime)
              .build();

      String emailContent = emailGenerator.generateVerificationEmail(emailVerificationData, recipientEmail);
      emailSenderService.sendEmail(recipientEmail, EmailSubjects.EMAIL_VERIFICATION, emailContent);
      log.info("Verification email sent to: {}", recipientEmail);
    } catch (EmailProcessingException e) {
      log.error("Validation failed while sending verification email: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Failed to send verification email to: {}", emailRequest.getRecipientEmail(), e);
      throw new EmailProcessingException("Unexpected error during email processing", e);
    }
  }
}
