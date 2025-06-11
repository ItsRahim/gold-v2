package com.rahim.emailservice.service;

import com.rahim.emailservice.BaseTestConfiguration;
import com.rahim.emailservice.service.impl.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @created 11/06/2025
 * @author Rahim Ahmed
 */
public class EmailSenderServiceTest extends BaseTestConfiguration {
  @Autowired private EmailSenderService emailSenderService;
}
