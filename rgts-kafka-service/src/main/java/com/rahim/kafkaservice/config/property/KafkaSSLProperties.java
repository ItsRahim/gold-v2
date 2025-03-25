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
@ConfigurationProperties(prefix = "kafka.ssl")
public class KafkaSSLProperties {
  private String protocol;
  private String keystoreFile;
  private String keystorePassword;
  private String truststoreFile;
  private String truststorePassword;

  @PostConstruct
  public void validateSSLProperties() {
    if (protocol != null && !protocol.isEmpty() && protocol.equalsIgnoreCase("ssl")) {
      if (keystoreFile == null || keystoreFile.isEmpty()) {
        throw new InvalidKafkaConfigurationException("Keystore file is required when using SSL.");
      }

      if (keystorePassword == null || keystorePassword.isEmpty()) {
        throw new InvalidKafkaConfigurationException(
            "Keystore password is required when using SSL.");
      }

      if (truststoreFile == null || truststoreFile.isEmpty()) {
        throw new InvalidKafkaConfigurationException("Truststore file is required when using SSL.");
      }

      if (truststorePassword == null || truststorePassword.isEmpty()) {
        throw new InvalidKafkaConfigurationException(
            "Truststore password is required when using SSL.");
      }
    }
  }
}
