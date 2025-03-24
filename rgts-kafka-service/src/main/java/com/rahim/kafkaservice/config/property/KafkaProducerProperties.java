package com.rahim.kafkaservice.config.property;

import com.rahim.kafkaservice.exception.InvalidKafkaConfigurationException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author Rahim Ahmed
 * @created 17/03/2025
 */
@Getter
@Setter
@Component
@Profile("!test")
@ConfigurationProperties(prefix = "kafka.producer")
public class KafkaProducerProperties {
    private String keySerializer;
    private String valueSerializer;

    @PostConstruct
    public void validateProducerProperties() {
        if (keySerializer == null || keySerializer.isEmpty()) {
            throw new InvalidKafkaConfigurationException("Kafka Producer Properties missing required property 'kafka.producer.key-serializer'");
        }

        if (valueSerializer == null || valueSerializer.isEmpty()) {
            throw new InvalidKafkaConfigurationException("Kafka Producer Properties missing required property 'kafka.producer.value-serializer'");
        }
    }
}
