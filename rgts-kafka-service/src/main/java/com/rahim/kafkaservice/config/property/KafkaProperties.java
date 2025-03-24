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
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
    private String securityProtocol;
    private String bootstrapServers;

    @PostConstruct
    public void init() {
        if (securityProtocol == null) {
            throw new InvalidKafkaConfigurationException("Kafka Properties missing required property 'kafka.security-protocol'");
        }

        if (bootstrapServers == null) {
            throw new InvalidKafkaConfigurationException("Kafka Properties missing required property 'kafka.bootstrap-servers'");
        }
    }
}
