package com.rahim.storageservice.configuration.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @created 27/06/2025
 * @author Rahim Ahmed
 */
@Getter
@Setter
public class MinioProperties {
  private String endpoint;
  private String accessKey;
  private String secretKey;
  private boolean secure = true;
}
