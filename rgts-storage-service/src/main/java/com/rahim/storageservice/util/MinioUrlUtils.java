package com.rahim.storageservice.util;

import com.rahim.storageservice.exception.MinioStorageException;
import com.rahim.storageservice.model.BucketAndKey;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;

/**
 * @created 29/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
public class MinioUrlUtils {

  public static BucketAndKey extractBucketAndKey(String url) {
    log.debug("Extracting bucket and key from URL: {}", url);

    if (url == null || url.trim().isEmpty()) {
      log.error("URL extraction failed: URL is null or empty");
      throw new MinioStorageException("MinIO URL cannot be null or empty");
    }

    try {
      URI uri = new URI(url.trim());
      String path = uri.getPath();

      if (path == null || path.isEmpty()) {
        log.error("URL extraction failed: path is null or empty for URL: {}", url);
        throw new MinioStorageException("Invalid MinIO URL format - no path found");
      }

      String cleanPath = path.startsWith("/") ? path.substring(1) : path;
      String[] parts = cleanPath.split("/", 2);

      if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
        log.error("URL extraction failed: insufficient parts in path '{}' for URL: {}", path, url);
        throw new MinioStorageException(
            "Invalid MinIO URL format - expected format: /bucket/objectKey");
      }

      String bucketName = parts[0].trim();
      String objectKey = parts[1].trim();

      log.debug("Successfully extracted - Bucket: {}, ObjectKey: {}", bucketName, objectKey);
      return new BucketAndKey(bucketName, objectKey);

    } catch (MinioStorageException e) {
      throw e;
    } catch (Exception e) {
      log.error("URL extraction failed for URL '{}': {}", url, e.getMessage());
      throw new MinioStorageException("Failed to parse MinIO URL: " + e.getMessage());
    }
  }
}
