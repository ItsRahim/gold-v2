package com.rahim.storageservice.util;

import com.rahim.storageservice.model.BucketAndKey;
import java.net.URI;

public class MinioUrlUtils {
  public static BucketAndKey extractBucketAndKey(String url) {
    try {
      URI uri = new URI(url);
      String path = uri.getPath();
      String[] parts = path.startsWith("/") ? path.substring(1).split("/", 2) : path.split("/", 2);

      if (parts.length < 2) {
        throw new IllegalArgumentException("Invalid MinIO URL format");
      }

      return new BucketAndKey(parts[0], parts[1]);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to parse MinIO URL: " + e.getMessage(), e);
    }
  }
}
