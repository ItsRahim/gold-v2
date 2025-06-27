package com.rahim.storageservice.configuration;

import com.rahim.storageservice.configuration.properties.AwsProperties;
import com.rahim.storageservice.configuration.properties.MinioProperties;
import com.rahim.storageservice.configuration.properties.StorageProvider;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @created 27/06/2025
 * @author Rahim Ahmed
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {
  private StorageProvider provider;
  private String defaultBucket;
  private AwsProperties aws = new AwsProperties();
  private MinioProperties minio = new MinioProperties();
}
