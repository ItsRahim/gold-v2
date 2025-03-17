package com.rahim.kafkaservice.config;

import com.rahim.kafkaservice.config.property.KafkaProperties;
import com.rahim.kafkaservice.config.property.KafkaSSLProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Rahim Ahmed
 * @created 17/03/2025
 */
@RequiredArgsConstructor
public class KafkaBaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaBaseConfig.class);
    protected final KafkaSSLProperties kafkaSSLProperties;
    protected final KafkaProperties kafkaProperties;

    public boolean isSSLEnabled() {
        String securityProtocol = kafkaProperties.getSecurityProtocol();
        return securityProtocol != null && securityProtocol.equalsIgnoreCase("SSL");
    }

    public void validateSSLProps(Map<String, Object> kafkaProperties) {
        boolean anyNull = kafkaProperties
                .values()
                .stream()
                .anyMatch(Objects::isNull);

        if (anyNull) {
            String nullFields = kafkaProperties.entrySet().stream()
                    .filter(entry -> entry.getValue() == null)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.joining(", "));
            logger.error("Null SSL properties detected: {}. Verify property values in appConfig.yml", nullFields);
            throw new IllegalArgumentException("Incomplete Kafka SSL configuration. Properties must not be null");
        }
    }
}
