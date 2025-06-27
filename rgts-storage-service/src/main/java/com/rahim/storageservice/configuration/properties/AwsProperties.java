package com.rahim.storageservice.configuration.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @created 27/06/2025
 * @author Rahim Ahmed
 */
@Setter
@Getter
public class AwsProperties {
  private String accessKey;
  private String secretKey;
  private String region;
  private String endpoint;
  private boolean pathStyleAccess = false;
}
