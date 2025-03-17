package com.rahim.kafkaservice.config;

import com.rahim.kafkaservice.config.property.KafkaConsumerProperties;
import com.rahim.kafkaservice.config.property.KafkaProperties;
import com.rahim.kafkaservice.config.property.KafkaSSLProperties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rahim Ahmed
 * @created 17/03/2025
 */
@Configuration
public class KafkaConsumerConfig extends KafkaBaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerConfig.class);
    private final KafkaConsumerProperties kafkaConsumerProperties;

    public KafkaConsumerConfig(KafkaProperties kafkaProperties, KafkaSSLProperties kafkaSSLProperties, KafkaConsumerProperties kafkaConsumerProperties) {
        super(kafkaSSLProperties, kafkaProperties);
        this.kafkaConsumerProperties = kafkaConsumerProperties;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConsumerProperties.getKeyDeserializer());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaConsumerProperties.getValueDeserializer());

        if (isSSLEnabled()) {
            logger.info("SSL Protocol Detected. Configuring Kafka Consumer Factory in SSL");
            configureSSLProps(consumerProps);
            validateSSLProps(consumerProps);
        } else {
            consumerProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        }

        logger.debug("Initialised Kafka Consumer Factory with: {}", consumerProps);
        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }

    private void configureSSLProps(Map<String, Object> consumerProps) {
        consumerProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaProperties.getSecurityProtocol());
        consumerProps.put(SslConfigs.SSL_PROTOCOL_CONFIG, kafkaSSLProperties.getProtocol());
        consumerProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, kafkaSSLProperties.getTruststoreFile());
        consumerProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaSSLProperties.getTruststorePassword());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }
}