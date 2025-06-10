package com.rahim.emailservice.service.impl;

import com.rahim.emailservice.exception.EmailSendingException;
import com.rahim.emailservice.service.IEmailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @created 10/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderService implements IEmailSenderService {
  private final JavaMailSender javaMailSender;

  @Value("${spring.mail.username}")
  private String senderEmail;

  @Override
  public void sendEmail(String recipientEmail, String subject, String emailContent) {
    log.debug("Attempting to send email - To: {}, Subject: {}", recipientEmail, subject);

    validateEmailParameters(recipientEmail, subject, emailContent);

    try {
      MimeMessage mimeMessage = createMimeMessage(recipientEmail, subject, emailContent);
      javaMailSender.send(mimeMessage);
    } catch (MessagingException e) {
      log.error("Failed to create email message for recipient: {}", recipientEmail, e);
      throw new EmailSendingException("Failed to create email message");
    } catch (Exception e) {
      log.error("Failed to send email - To: {}, Subject: {}", recipientEmail, subject, e);
      throw new EmailSendingException("Failed to send email", e);
    }
  }

  private void validateEmailParameters(String recipientEmail, String subject, String emailContent) {
    if (!StringUtils.hasText(recipientEmail)) {
      throw new EmailSendingException("Recipient email cannot be empty");
    }
    if (!StringUtils.hasText(subject)) {
      throw new EmailSendingException("Email subject cannot be empty");
    }
    if (!StringUtils.hasText(emailContent)) {
      throw new EmailSendingException("Email content cannot be empty");
    }
    if (!StringUtils.hasText(senderEmail)) {
      throw new EmailSendingException("Sender email is not configured");
    }
  }

  private MimeMessage createMimeMessage(String recipientEmail, String subject, String emailContent)
      throws MessagingException {

    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper messageHelper =
        new MimeMessageHelper(
            mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, UTF_8.name());

    messageHelper.setFrom(senderEmail);
    messageHelper.setTo(recipientEmail);
    messageHelper.setSubject(subject);
    messageHelper.setText(emailContent, true);

    log.debug(
        "Created MIME message - From: {}, To: {}, Subject: {}",
        senderEmail,
        recipientEmail,
        subject);

    return mimeMessage;
  }
}
