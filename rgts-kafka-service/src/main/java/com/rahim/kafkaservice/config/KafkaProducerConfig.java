package com.rahim.kafkaservice.config;

import com.rahim.kafkaservice.config.property.KafkaProducerProperties;
import com.rahim.kafkaservice.config.property.KafkaProperties;
import com.rahim.kafkaservice.config.property.KafkaSSLProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rahim Ahmed
 * @created 17/03/2025
 */
@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerConfig.class);
    private final KafkaProducerProperties kafkaProducerProperties;
    private final KafkaSSLProperties kafkaSSLProperties;
    private final KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<String, byte[]> producerFactory() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerProperties.getKeySerializer());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerProperties.getValueSerializer());

        if (kafkaProperties.getSecurityProtocol().equals("SSL")) {
            logger.info("SSL Protocol Detected. Configuring Kafka Producer Factory in SSL");
            producerProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaProperties.getSecurityProtocol());
            producerProps.put(SslConfigs.SSL_PROTOCOL_CONFIG, kafkaSSLProperties.getProtocol());
            producerProps.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, kafkaSSLProperties.getKeystoreFile());
            producerProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, kafkaSSLProperties.getKeystorePassword());
            producerProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, kafkaSSLProperties.getTruststoreFile());
            producerProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaSSLProperties.getTruststorePassword());
        } else {
            producerProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        }

        logger.debug("Initialised Kafka Producer Factory with: {}", producerProps);
        return new DefaultKafkaProducerFactory<>(producerProps);
    }

    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}