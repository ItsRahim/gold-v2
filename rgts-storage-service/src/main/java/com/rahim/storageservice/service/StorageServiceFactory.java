package com.rahim.storageservice.service;

import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @created 28/06/2025
 * @author Rahim Ahmed
 */
@Component
@AllArgsConstructor
public class StorageServiceFactory {
  private final Map<String, StorageService> storageServices;

  @Value("${storage.provider}")
  private final String storageProvider;

  public StorageService getStorageService() {
    StorageService storageService = storageServices.get(storageProvider);
    if (storageService == null) {
      throw new IllegalArgumentException(
          "No storage service found for provider: " + storageProvider);
    }

    return storageService;
  }
}
