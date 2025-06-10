package com.rahim.emailservice.service;

import com.rahim.proto.protobuf.email.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * @created 10/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

  @KafkaListener(topics = "email-request", groupId = "email-service-group")
  public void consumeEmailRequest(@Payload byte[] emailRequest, Acknowledgment ack) {
    try {
      EmailRequest request = EmailRequest.parseFrom(emailRequest);
      log.info("Received email request: {}", request);

    } catch (Exception e) {
      log.error("Failed to process email request", e);
    }
  }
}
