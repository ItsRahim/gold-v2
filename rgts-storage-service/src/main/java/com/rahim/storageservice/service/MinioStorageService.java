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
    log.info("Attempting to create bucket: {}", bucketName);

    try {
      if (bucketExists(bucketName)) {
        log.info("Bucket '{}' already exists, skipping creation", bucketName);
        return;
      }

      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
      log.info("Successfully created bucket: {}", bucketName);

    } catch (StorageException | MinioStorageException e) {
      log.error(
          "Storage operation failed while creating bucket '{}': {}", bucketName, e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Failed to create bucket '{}': {}", bucketName, e.getMessage(), e);
      throw new MinioStorageException("Failed to create bucket: " + bucketName);
    }
  }

  @Override
  public void deleteBucket(String bucketName) {
    validateBucketName(bucketName);
    log.info("Attempting to delete bucket: {}", bucketName);

    try {
      if (!bucketExists(bucketName)) {
        log.warn("Bucket '{}' does not exist, cannot delete", bucketName);
        throw new MinioStorageException("Bucket does not exist: " + bucketName);
      }

      minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
      log.info("Successfully deleted bucket: {}", bucketName);

    } catch (StorageException | MinioStorageException e) {
      log.error(
          "Storage operation failed while deleting bucket '{}': {}", bucketName, e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Failed to delete bucket '{}': {}", bucketName, e.getMessage(), e);
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
      log.error("Failed to check if bucket '{}' exists: {}", bucketName, e.getMessage(), e);
      throw new MinioStorageException("Failed to check bucket existence: " + bucketName);
    }
  }

  @Override
  public String uploadImage(String bucketName, String id, MultipartFile file) {
    validateBucketName(bucketName);
    validateUploadParameters(id, file);

    log.info(
        "Starting image upload - Bucket: {}, ID: {}, FileName: {}, FileSize: {} bytes, ContentType: {}",
        bucketName,
        id,
        file.getOriginalFilename(),
        file.getSize(),
        file.getContentType());

    if (!bucketExists(bucketName)) {
      log.info("Bucket '{}' does not exist. Creating it now.", bucketName);
      createBucket(bucketName);
    }

    String objectKey = null;
    try (InputStream inputStream = file.getInputStream()) {
      objectKey = StorageUrlUtils.generateObjectKeyFromId(id, file);
      log.debug("Generated object key: {}", objectKey);

      if (objectExists(bucketName, objectKey)) {
        log.warn(
            "Object '{}' already exists in bucket '{}', will overwrite", objectKey, bucketName);
      }

      minioClient.putObject(
          PutObjectArgs.builder().bucket(bucketName).object(objectKey).stream(
                  inputStream, file.getSize(), -1)
              .contentType(file.getContentType())
              .build());

      String imageUrl = String.format("%s/%s/%s", minioUrl, bucketName, objectKey);
      log.info(
          "Successfully uploaded image - Bucket: {}, ObjectKey: {}, URL: {}",
          bucketName,
          objectKey,
          imageUrl);
      return imageUrl;

    } catch (StorageException e) {
      log.error(
          "Upload failed due to storage utility error - Bucket: {}, ObjectKey: {}, Error: {}",
          bucketName,
          objectKey,
          e.getMessage());
      throw e;
    } catch (MinioStorageException e) {
      log.error(
          "Upload failed due to MinIO error - Bucket: {}, ObjectKey: {}, Error: {}",
          bucketName,
          objectKey,
          e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error(
          "Failed to upload object to bucket '{}' with key '{}': {}",
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

    log.info("Attempting to download object - Bucket: {}, ObjectKey: {}", bucketName, objectKey);

    try {
      if (!objectExists(bucketName, objectKey)) {
        log.warn("Object '{}' does not exist in bucket '{}'", objectKey, bucketName);
        throw new MinioStorageException("Object does not exist: " + objectKey);
      }

      InputStream inputStream =
          minioClient.getObject(
              GetObjectArgs.builder().bucket(bucketName).object(objectKey).build());

      log.info(
          "Successfully initiated download - Bucket: {}, ObjectKey: {}", bucketName, objectKey);
      return inputStream;

    } catch (MinioStorageException e) {
      log.error(
          "MinIO operation failed while downloading object '{}' from bucket '{}': {}",
          objectKey,
          bucketName,
          e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error(
          "Failed to download object '{}' from bucket '{}': {}",
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
    log.info("Generating presigned URL for: {}", imageUrl);

    try {
      BucketAndKey bucketAndKey = MinioUrlUtils.extractBucketAndKey(imageUrl);
      log.debug(
          "Extracted bucket and key - Bucket: {}, Key: {}",
          bucketAndKey.getBucketName(),
          bucketAndKey.getObjectKey());

      if (!objectExists(bucketAndKey.getBucketName(), bucketAndKey.getObjectKey())) {
        log.warn(
            "Object '{}' does not exist in bucket '{}'",
            bucketAndKey.getObjectKey(),
            bucketAndKey.getBucketName());
        throw new MinioStorageException("Object does not exist for URL: " + imageUrl);
      }

      String presignedUrl =
          minioClient.getPresignedObjectUrl(
              GetPresignedObjectUrlArgs.builder()
                  .method(Method.GET)
                  .bucket(bucketAndKey.getBucketName())
                  .object(bucketAndKey.getObjectKey())
                  .expiry(60 * 60)
                  .build());

      log.info(
          "Successfully generated presigned URL for object - Bucket: {}, Key: {}",
          bucketAndKey.getBucketName(),
          bucketAndKey.getObjectKey());
      return presignedUrl;

    } catch (StorageException | MinioStorageException e) {
      log.error(
          "Storage operation failed while generating presigned URL for '{}': {}",
          imageUrl,
          e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Failed to generate presigned URL for '{}': {}", imageUrl, e.getMessage(), e);
      throw new MinioStorageException("Failed to generate presigned URL");
    }
  }

  @Override
  public boolean objectExists(String bucketName, String objectKey) {
    validateBucketName(bucketName);
    validateObjectKey(objectKey);

    log.debug("Checking if object exists - Bucket: {}, ObjectKey: {}", bucketName, objectKey);

    try {
      minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectKey).build());
      log.debug("Object '{}' exists in bucket '{}'", objectKey, bucketName);
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

    log.info("Attempting to delete object from bucket '{}' using URL: {}", bucketName, url);

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

      log.info("Successfully deleted object '{}' from bucket '{}'", objectKey, bucketName);

    } catch (StorageException | MinioStorageException e) {
      log.error(
          "Storage operation failed while deleting object from bucket '{}': {}",
          bucketName,
          e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error(
          "Failed to delete object from bucket '{}' using URL '{}': {}",
          bucketName,
          url,
          e.getMessage(),
          e);
      throw new MinioStorageException("Failed to delete object from bucket: " + bucketName);
    }
  }

  private void validateBucketName(String bucketName) {
    if (bucketName == null || bucketName.trim().isEmpty()) {
      log.warn("Bucket name validation failed: bucket name is null or empty");
      throw new MinioStorageException("Bucket name cannot be null or empty");
    }
  }

  private void validateObjectKey(String objectKey) {
    if (objectKey == null || objectKey.trim().isEmpty()) {
      log.warn("Object key validation failed: object key is null or empty");
      throw new MinioStorageException("Object key cannot be null or empty");
    }
  }

  private void validateImageUrl(String url) {
    if (url == null || url.trim().isEmpty()) {
      log.warn("URL validation failed: URL is null or empty");
      throw new MinioStorageException("URL cannot be null or empty");
    }
  }

  private void validateUploadParameters(String id, MultipartFile file) {
    if (id == null || id.trim().isEmpty()) {
      log.warn("Upload validation failed: ID is null or empty");
      throw new MinioStorageException("ID cannot be null or empty");
    }

    if (file == null) {
      log.warn("Upload validation failed: file is null");
      throw new MinioStorageException("File cannot be null");
    }

    if (file.isEmpty()) {
      log.warn("Upload validation failed: file is empty");
      throw new MinioStorageException("File cannot be empty");
    }

    if (file.getSize() <= 0) {
      log.warn("Upload validation failed: file size is {} bytes", file.getSize());
      throw new MinioStorageException("File size must be greater than 0");
    }
  }
}
