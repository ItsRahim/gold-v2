package com.rahim.kafkaservice.service;

import com.google.protobuf.Message;
import com.rahim.proto.util.ProtobufDerSerUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
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
public class ProtobufKafkaService implements IKafkaService {
    private static final Logger logger = LoggerFactory.getLogger(ProtobufKafkaService.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Override
    public void sendMessage(String topic, Message protobufMessage) {
        if (protobufMessage == null) {
            logger.error("Protobuf message is null");
            return;
        }

        byte[] serializedData = ProtobufDerSerUtil.serialiseProtobufToByteArray(protobufMessage);
        org.springframework.messaging.Message<byte[]> message = generateMessage(topic, serializedData);

        CompletableFuture<SendResult<String, byte[]>> future = kafkaTemplate.send(message);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                logger.error("Error sending message to topic '{}': {}", topic, ex.getMessage());
            }
        });
    }

    private org.springframework.messaging.Message<byte[]> generateMessage(String topic, byte[] data) {
        return MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, UUID.randomUUID().toString())
                .setHeader(KafkaHeaders.TIMESTAMP, System.currentTimeMillis())
                .build();
    }
}
