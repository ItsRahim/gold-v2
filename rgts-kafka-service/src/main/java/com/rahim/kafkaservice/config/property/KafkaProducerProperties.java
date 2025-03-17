package com.rahim.kafkaservice.config.property;

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
@ConfigurationProperties(prefix = "kafka.producer")
public class KafkaProducerProperties {
    private String keySerializer;
    private String valueSerializer;
}
