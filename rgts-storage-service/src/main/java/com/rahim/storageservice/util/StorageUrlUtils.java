package com.rahim.storageservice.util;

import com.rahim.storageservice.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

/**
 * @created 29/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
public class StorageUrlUtils {

  public static String generateObjectKeyFromId(String id, MultipartFile file) {
    log.debug(
        "Generating object key from ID: {} and file: {}",
        id,
        file != null ? file.getOriginalFilename() : "null");

    if (id == null || id.trim().isEmpty()) {
      log.error("Object key generation failed: ID is null or empty");
      throw new StorageException("ID cannot be null or empty");
    }

    if (file == null) {
      log.error("Object key generation failed: file is null");
      throw new StorageException("File cannot be null");
    }

    try {
      String extension = getFileExtension(file);
      String objectKey = id.trim() + extension;
      log.debug("Generated object key: {}", objectKey);
      return objectKey;

    } catch (StorageException e) {
      throw e;
    } catch (Exception e) {
      log.error("Failed to generate object key from ID '{}': {}", id, e.getMessage());
      throw new StorageException("Failed to generate object key: " + e.getMessage());
    }
  }

  public static String getFileExtension(MultipartFile file) {
    if (file == null) {
      log.error("File extension extraction failed: file is null");
      throw new StorageException("File cannot be null");
    }

    log.debug(
        "Extracting file extension from file: {}, contentType: {}",
        file.getOriginalFilename(),
        file.getContentType());

    try {
      String originalFilename = file.getOriginalFilename();
      if (originalFilename != null
          && !originalFilename.trim().isEmpty()
          && originalFilename.contains(".")) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!extension.trim().isEmpty() && extension.length() > 1) {
          log.debug("Extension extracted from filename: {}", extension);
          return extension.toLowerCase();
        }
      }

      String contentType = file.getContentType();
      if (contentType != null && !contentType.trim().isEmpty()) {
        String extension =
            switch (contentType.toLowerCase()) {
              case "image/png" -> ".png";
              case "image/gif" -> ".gif";
              case "image/webp" -> ".webp";
              case "image/bmp" -> ".bmp";
              case "image/tiff" -> ".tiff";
              case "image/svg+xml" -> ".svg";
              default -> ".jpg";
            };
        log.debug("Extension extracted from content type '{}': {}", contentType, extension);
        return extension;
      }

      log.warn(
          "Could not determine file extension from filename '{}' or content type '{}', using default .jpg",
          originalFilename,
          contentType);
      return ".jpg";

    } catch (Exception e) {
      log.error("Failed to extract file extension: {}", e.getMessage());
      throw new StorageException("Failed to extract file extension: " + e.getMessage());
    }
  }

  public static String extractObjectKeyFromUrl(String url) {
    if (url == null || url.trim().isEmpty()) {
      log.error("Object key extraction failed: URL is null or empty");
      throw new StorageException("URL cannot be null or empty");
    }

    try {

      String cleanUrl = url.trim();
      if (cleanUrl.contains("?")) {
        cleanUrl = cleanUrl.substring(0, cleanUrl.indexOf("?"));
      }
      if (cleanUrl.contains("#")) {
        cleanUrl = cleanUrl.substring(0, cleanUrl.indexOf("#"));
      }

      String[] parts = cleanUrl.split("/");
      if (parts.length == 0) {
        log.error("Object key extraction failed: no parts found in URL: {}", url);
        throw new StorageException("Invalid URL format - cannot extract object key");
      }

      String objectKey = parts[parts.length - 1];
      if (objectKey.trim().isEmpty()) {
        log.error("Object key extraction failed: extracted key is empty from URL: {}", url);
        throw new StorageException("Invalid URL format - object key is empty");
      }

      return objectKey.trim();
    } catch (StorageException e) {
      throw e;
    } catch (Exception e) {
      log.error("Failed to extract object key from URL '{}': {}", url, e.getMessage());
      throw new StorageException("Failed to extract object key from URL: " + e.getMessage());
    }
  }
}
