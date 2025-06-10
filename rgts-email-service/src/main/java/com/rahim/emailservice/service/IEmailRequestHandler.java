package com.rahim.emailservice.service;

import com.rahim.proto.protobuf.email.EmailRequest;

/**
 * @created 10/06/2025
 * @author Rahim Ahmed
 */
public interface IEmailRequestHandler {
    void handleEmailRequest(EmailRequest emailRequest);
}
