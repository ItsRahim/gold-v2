package com.rahim.storageservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @created 28/06/2025
 * @author Rahim Ahmed
 */
@Component
@RequiredArgsConstructor
public class StorageServiceFactory {
  private final ApplicationContext applicationContext;

  @Value("${storage.provider}")
  private String storageProvider;

  public StorageService getStorageService() {
    try {
      return applicationContext.getBean(storageProvider, StorageService.class);
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "No storage service found for provider: " + storageProvider, e);
    }
  }
}
