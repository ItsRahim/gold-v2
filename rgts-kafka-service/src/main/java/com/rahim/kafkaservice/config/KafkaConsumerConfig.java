package com.rahim.kafkaservice.config;

import com.rahim.kafkaservice.config.property.KafkaConsumerProperties;
import com.rahim.kafkaservice.config.property.KafkaProperties;
import com.rahim.kafkaservice.config.property.KafkaSSLProperties;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

/**
 * @author Rahim Ahmed
 * @created 17/03/2025
 */
@Slf4j
@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class KafkaConsumerConfig {
  private final KafkaConsumerProperties kafkaConsumerProperties;
  private final KafkaSSLProperties kafkaSSLProperties;
  private final KafkaProperties kafkaProperties;

  @Bean
  public ConsumerFactory<String, byte[]> consumerFactory() {
    Map<String, Object> consumerProps = new HashMap<>();
    consumerProps.put(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());

    consumerProps.put(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConsumerProperties.getKeyDeserializer());
    consumerProps.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        kafkaConsumerProperties.getValueDeserializer());

    if ("SSL".equalsIgnoreCase(kafkaProperties.getSecurityProtocol())) {
      log.info("SSL Protocol Detected. Configuring Kafka Consumer Factory in SSL");
      consumerProps.put(
          CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaProperties.getSecurityProtocol());
      consumerProps.put(SslConfigs.SSL_PROTOCOL_CONFIG, kafkaSSLProperties.getProtocol());
      consumerProps.put(
          SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, kafkaSSLProperties.getTruststoreFile());
      consumerProps.put(
          SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaSSLProperties.getTruststorePassword());
    } else {
      consumerProps.put(
          CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaProperties.getSecurityProtocol());
    }

    log.debug("Initialised Kafka Consumer Factory with: {}", consumerProps);
    return new DefaultKafkaConsumerFactory<>(consumerProps);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, byte[]> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

    return factory;
  }
}
