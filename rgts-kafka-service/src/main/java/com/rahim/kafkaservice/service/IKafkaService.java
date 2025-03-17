package com.rahim.kafkaservice.service;

/**
 * @author Rahim Ahmed
 * @created 17/03/2025
 */
public interface IKafkaService {
    <T> void sendMessage(String topic, T data);
}
