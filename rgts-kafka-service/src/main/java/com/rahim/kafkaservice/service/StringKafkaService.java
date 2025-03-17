package com.rahim.kafkaservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Rahim Ahmed
 * @created 17/03/2025
 */
@Service
@RequiredArgsConstructor
public class StringKafkaService implements IKafkaService {
    private static final Logger logger = LoggerFactory.getLogger(StringKafkaService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public <T> void sendMessage(String topic, T data) {
        if (data instanceof String messageData) {
            Message<String> message = generateMessage(topic, messageData);

            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(message);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    logger.error("Error sending message to topic '{}': {}", topic, ex.getMessage());
                }
            });
        } else {
            logger.error("Message data is not of type String: {}", data.getClass().getName());
        }
    }

    private <T> Message<T> generateMessage(String topic, T data) {
        return MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, UUID.randomUUID().toString())
                .setHeader(KafkaHeaders.TIMESTAMP, System.currentTimeMillis())
                .build();
    }
}
