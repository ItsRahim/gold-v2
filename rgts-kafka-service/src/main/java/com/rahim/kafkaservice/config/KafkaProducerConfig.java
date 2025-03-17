package com.rahim.kafkaservice.config;

import com.rahim.kafkaservice.config.property.KafkaProducerProperties;
import com.rahim.kafkaservice.config.property.KafkaProperties;
import com.rahim.kafkaservice.config.property.KafkaSSLProperties;
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
public class KafkaProducerConfig extends KafkaBaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerConfig.class);
    private final KafkaProducerProperties kafkaProducerProperties;

    public KafkaProducerConfig(KafkaProperties kafkaProperties, KafkaSSLProperties kafkaSSLProperties, KafkaProducerProperties kafkaProducerProperties) {
        super(kafkaSSLProperties, kafkaProperties);
        this.kafkaProducerProperties = kafkaProducerProperties;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerProperties.getKeySerializer());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerProperties.getValueSerializer());

        if (isSSLEnabled()) {
            logger.info("SSL Protocol Detected. Configuring Kafka Producer Factory in SSL");
            configureSSLProps(producerProps);
            validateSSLProps(producerProps);
        } else {
            producerProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        }

        logger.debug("Initialised Kafka Producer Factory with: {}", producerProps);
        return new DefaultKafkaProducerFactory<>(producerProps);
    }

    private void configureSSLProps(Map<String, Object> producerProps) {
        producerProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaProperties.getSecurityProtocol());
        producerProps.put(SslConfigs.SSL_PROTOCOL_CONFIG, kafkaSSLProperties.getProtocol());
        producerProps.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, kafkaSSLProperties.getKeystoreFile());
        producerProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, kafkaSSLProperties.getKeystorePassword());
        producerProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, kafkaSSLProperties.getTruststoreFile());
        producerProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaSSLProperties.getTruststorePassword());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}