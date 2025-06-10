package com.rahim.emailservice.service;

/**
 * @created 10/06/2025
 * @author Rahim Ahmed
 */
public interface IEmailSenderService {
    void sendEmail(String recipientEmail, String subject, String emailContent);
}
