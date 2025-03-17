package com.rahim.kafkaservice.config;

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
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
    private String securityProtocol;
    private String bootstrapServers;
}
