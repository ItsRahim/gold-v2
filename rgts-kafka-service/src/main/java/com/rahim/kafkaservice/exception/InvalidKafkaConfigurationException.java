package com.rahim.kafkaservice.exception;

/**
 * @author Rahim Ahmed
 * @created 18/03/2025
 */
public class InvalidKafkaConfigurationException extends RuntimeException {

    public InvalidKafkaConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidKafkaConfigurationException(String message) {
        super(message);
    }
}
