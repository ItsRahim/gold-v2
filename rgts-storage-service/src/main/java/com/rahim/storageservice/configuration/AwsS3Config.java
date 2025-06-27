package com.rahim.storageservice.configuration;

import com.rahim.storageservice.configuration.properties.AwsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * @created 27/06/2025
 * @author Rahim Ahmed
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "storage.provider", havingValue = "AWS_S3")
public class AwsS3Config {
  private final StorageProperties storageProperties;

  @Bean
  public S3Client s3Client() {
    AwsProperties aws = storageProperties.getAws();

    AwsBasicCredentials credentials =
        AwsBasicCredentials.create(aws.getAccessKey(), aws.getSecretKey());

    S3ClientBuilder builder =
        S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of(aws.getRegion()));

    if (aws.getEndpoint() != null && !aws.getEndpoint().trim().isEmpty()) {
      builder.endpointOverride(URI.create(aws.getEndpoint()));
    }

    if (aws.isPathStyleAccess()) {
      builder.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build());
    }

    return builder.build();
  }
}
