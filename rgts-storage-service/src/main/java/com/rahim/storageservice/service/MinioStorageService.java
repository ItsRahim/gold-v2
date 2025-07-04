package com.rahim.storageservice.service;

import com.rahim.storageservice.exception.MinioStorageException;
import com.rahim.storageservice.exception.StorageException;
import com.rahim.storageservice.model.BucketAndKey;
import com.rahim.storageservice.util.MinioUrlUtils;
import com.rahim.storageservice.util.StorageUrlUtils;
import io.minio.*;
import io.minio.http.Method;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @created 27/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@RequiredArgsConstructor
@Service("MINIO")
public class MinioStorageService implements StorageService {
  private final MinioClient minioClient;
  private final String minioUrl;

  @Override
  public void createBucket(String bucketName) {
    validateBucketName(bucketName);
    log.debug("Creating bucket: {}", bucketName);

    try {
      if (bucketExists(bucketName)) {
        log.debug("Bucket '{}' already exists, skipping creation", bucketName);
        return;
      }

      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
      log.info("Created bucket: {}", bucketName);

    } catch (StorageException | MinioStorageException e) {
      log.error("Failed to create bucket '{}': {}", bucketName, e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Unexpected error creating bucket '{}': {}", bucketName, e.getMessage(), e);
      throw new MinioStorageException("Failed to create bucket: " + bucketName);
    }
  }

  @Override
  public void deleteBucket(String bucketName) {
    validateBucketName(bucketName);
    log.debug("Deleting bucket: {}", bucketName);

    try {
      if (!bucketExists(bucketName)) {
        log.warn("Bucket '{}' does not exist, cannot delete", bucketName);
        throw new MinioStorageException("Bucket does not exist: " + bucketName);
      }

      minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
      log.info("Deleted bucket: {}", bucketName);

    } catch (StorageException | MinioStorageException e) {
      log.error("Failed to delete bucket '{}': {}", bucketName, e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Unexpected error deleting bucket '{}': {}", bucketName, e.getMessage(), e);
      throw new MinioStorageException("Failed to delete bucket: " + bucketName);
    }
  }

  @Override
  public boolean bucketExists(String bucketName) {
    validateBucketName(bucketName);
    log.debug("Checking if bucket '{}' exists", bucketName);

    try {
      boolean exists =
          minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
      log.debug("Bucket '{}' exists: {}", bucketName, exists);
      return exists;

    } catch (Exception e) {
      log.error(
          "Unexpected error checking bucket '{}' existence: {}", bucketName, e.getMessage(), e);
      throw new MinioStorageException("Failed to check bucket existence: " + bucketName);
    }
  }

  @Override
  public String uploadImage(String bucketName, String id, MultipartFile file) {
    validateBucketName(bucketName);
    validateUploadParameters(id, file);

    log.info(
        "Uploading image - Bucket: {}, ID: {}, Size: {} bytes", bucketName, id, file.getSize());

    if (!bucketExists(bucketName)) {
      log.info("Creating bucket '{}' as it does not exist", bucketName);
      createBucket(bucketName);
    }

    String objectKey = null;
    try (InputStream inputStream = file.getInputStream()) {
      objectKey = StorageUrlUtils.generateObjectKeyFromId(id, file);

      if (objectExists(bucketName, objectKey)) {
        log.warn("Overwriting existing object '{}' in bucket '{}'", objectKey, bucketName);
      }

      minioClient.putObject(
          PutObjectArgs.builder().bucket(bucketName).object(objectKey).stream(
                  inputStream, file.getSize(), -1)
              .contentType(file.getContentType())
              .build());

      String imageUrl = String.format("%s/%s/%s", minioUrl, bucketName, objectKey);
      log.info("Uploaded image - Bucket: {}, ObjectKey: {}", bucketName, objectKey);
      return imageUrl;

    } catch (StorageException | MinioStorageException e) {
      log.error(
          "Failed to upload to bucket '{}' with key '{}': {}",
          bucketName,
          objectKey,
          e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error(
          "Unexpected error uploading to bucket '{}' with key '{}': {}",
          bucketName,
          objectKey,
          e.getMessage(),
          e);
      throw new MinioStorageException("Failed to upload image to bucket: " + bucketName);
    }
  }

  @Override
  public InputStream download(String bucketName, String objectKey) {
    validateBucketName(bucketName);
    validateObjectKey(objectKey);

    log.debug("Downloading object - Bucket: {}, ObjectKey: {}", bucketName, objectKey);

    try {
      if (!objectExists(bucketName, objectKey)) {
        log.warn("Object '{}' does not exist in bucket '{}'", objectKey, bucketName);
        throw new MinioStorageException("Object does not exist: " + objectKey);
      }

      InputStream inputStream =
          minioClient.getObject(
              GetObjectArgs.builder().bucket(bucketName).object(objectKey).build());

      log.debug("Downloaded object - Bucket: {}, ObjectKey: {}", bucketName, objectKey);
      return inputStream;

    } catch (MinioStorageException e) {
      log.error(
          "Failed to download object '{}' from bucket '{}': {}",
          objectKey,
          bucketName,
          e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error(
          "Unexpected error downloading object '{}' from bucket '{}': {}",
          objectKey,
          bucketName,
          e.getMessage(),
          e);
      throw new MinioStorageException("Failed to download object from bucket: " + bucketName);
    }
  }

  @Override
  public String getPresignedUrl(String imageUrl) {
    validateImageUrl(imageUrl);

    try {
      BucketAndKey bucketAndKey = MinioUrlUtils.extractBucketAndKey(imageUrl);

      String presignedUrl =
          minioClient.getPresignedObjectUrl(
              GetPresignedObjectUrlArgs.builder()
                  .method(Method.GET)
                  .bucket(bucketAndKey.getBucketName())
                  .object(bucketAndKey.getObjectKey())
                  .expiry(60 * 60)
                  .build());

      log.trace(
          "Generated presigned URL for object - Bucket: {}, Key: {}",
          bucketAndKey.getBucketName(),
          bucketAndKey.getObjectKey());
      return presignedUrl;

    } catch (StorageException | MinioStorageException e) {
      log.error("Failed to generate presigned URL for '{}': {}", imageUrl, e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error(
          "Unexpected error generating presigned URL for '{}': {}", imageUrl, e.getMessage(), e);
      throw new MinioStorageException("Failed to generate presigned URL");
    }
  }

  @Override
  public boolean objectExists(String bucketName, String objectKey) {
    validateBucketName(bucketName);
    validateObjectKey(objectKey);

    try {
      minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectKey).build());
      return true;
    } catch (Exception e) {
      log.debug(
          "Object '{}' does not exist in bucket '{}': {}", objectKey, bucketName, e.getMessage());
      return false;
    }
  }

  @Override
  public void deleteObject(String bucketName, String url) {
    validateBucketName(bucketName);
    validateImageUrl(url);

    log.debug("Deleting object from bucket '{}' using URL: {}", bucketName, url);

    try {
      String objectKey = StorageUrlUtils.extractObjectKeyFromUrl(url);
      if (objectKey == null || objectKey.trim().isEmpty()) {
        throw new MinioStorageException("Cannot extract object key from URL: " + url);
      }

      if (!objectExists(bucketName, objectKey)) {
        log.warn(
            "Object '{}' does not exist in bucket '{}', nothing to delete", objectKey, bucketName);
        return;
      }

      minioClient.removeObject(
          RemoveObjectArgs.builder().bucket(bucketName).object(objectKey).build());

      log.info("Deleted object '{}' from bucket '{}'", objectKey, bucketName);

    } catch (StorageException | MinioStorageException e) {
      log.error("Failed to delete object from bucket '{}': {}", bucketName, e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error(
          "Unexpected error deleting object from bucket '{}' using URL '{}': {}",
          bucketName,
          url,
          e.getMessage(),
          e);
      throw new MinioStorageException("Failed to delete object from bucket: " + bucketName);
    }
  }

  private void validateBucketName(String bucketName) {
    if (bucketName == null || bucketName.trim().isEmpty()) {
      throw new MinioStorageException("Bucket name cannot be null or empty");
    }
  }

  private void validateObjectKey(String objectKey) {
    if (objectKey == null || objectKey.trim().isEmpty()) {
      throw new MinioStorageException("Object key cannot be null or empty");
    }
  }

  private void validateImageUrl(String url) {
    if (url == null || url.trim().isEmpty()) {
      throw new MinioStorageException("URL cannot be null or empty");
    }
  }

  private void validateUploadParameters(String id, MultipartFile file) {
    if (id == null || id.trim().isEmpty()) {
      throw new MinioStorageException("ID cannot be null or empty");
    }

    if (file == null) {
      throw new MinioStorageException("File cannot be null");
    }

    if (file.isEmpty()) {
      throw new MinioStorageException("File cannot be empty");
    }

    if (file.getSize() <= 0) {
      throw new MinioStorageException("File size must be greater than 0");
    }
  }
}
