package com.rahim.kafkaservice.config.property;

import com.rahim.kafkaservice.exception.InvalidKafkaConfigurationException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Rahim Ahmed
 * @created 17/03/2025
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kafka.consumer")
public class KafkaConsumerProperties {
    private String keyDeserializer;
    private String valueDeserializer;

    @PostConstruct
    public void validateConsumerProperties() {
        if (keyDeserializer == null || keyDeserializer.isEmpty()) {
            throw new InvalidKafkaConfigurationException("Kafka Consumer Properties missing required property 'kafka.consumer.key-deserializer'");
        }

        if (valueDeserializer == null || valueDeserializer.isEmpty()) {
            throw new InvalidKafkaConfigurationException("Kafka Consumer Properties missing required property 'kafka.consumer.value-deserializer'");
        }
    }
}
