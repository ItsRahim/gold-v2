package com.rahim.storageservice.configuration;

import com.rahim.storageservice.configuration.properties.MinioProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @created 27/06/2025
 * @author Rahim Ahmed
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "storage.provider", havingValue = "MINIO")
public class MinioConfig {
  private final StorageProperties storageProperties;

  @Bean
  public MinioClient minioClient() {
    MinioProperties minio = storageProperties.getMinio();

    return MinioClient.builder()
        .endpoint(minio.getEndpoint())
        .credentials(minio.getAccessKey(), minio.getSecretKey())
        .build();
  }

  @Bean
  public String minioUrl() {
    return storageProperties.getMinio().getEndpoint();
  }
}
