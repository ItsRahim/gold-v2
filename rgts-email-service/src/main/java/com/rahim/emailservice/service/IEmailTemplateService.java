package com.rahim.emailservice.service;

import com.rahim.emailservice.dto.EmailVerificationData;

/**
 * @created 10/06/2025
 * @author Rahim Ahmed
 */
public interface IEmailTemplateService {
  String generateVerificationEmail(EmailVerificationData emailVerificationData, String recipientEmail);
}
