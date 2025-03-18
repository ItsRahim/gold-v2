package com.rahim.kafkaservice.service;

import com.google.protobuf.Message;

/**
 * @author Rahim Ahmed
 * @created 17/03/2025
 */
public interface IKafkaService {
    void sendMessage(String topic, Message data);
}
