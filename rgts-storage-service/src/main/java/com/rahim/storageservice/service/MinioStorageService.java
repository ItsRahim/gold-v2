package com.rahim.storageservice.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @created 27/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {
  private final MinioClient minioClient;

  @Override
  public void createBucket(String bucketName) {
    try {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
    } catch (Exception e) {
      log.error("Failed to create bucket '{}': {}", bucketName, e.getMessage(), e);
    }
  }

  @Override
  public void deleteBucket(String bucketName) {
    try {
      minioClient.deleteBucketTags(DeleteBucketTagsArgs.builder().bucket(bucketName).build());
    } catch (Exception e) {
      log.error("Failed to delete bucket tags for '{}': {}", bucketName, e.getMessage(), e);
    }
  }

  @Override
  public boolean bucketExists(String bucketName) {
    try {
      return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    } catch (Exception e) {
      log.error("Failed to check if bucket '{}' exists: {}", bucketName, e.getMessage(), e);
      return false;
    }
  }

  @Override
  public void upload(String bucketName, String objectKey, MultipartFile file) {
    try (InputStream inputStream = file.getInputStream()) {
      minioClient.putObject(
          PutObjectArgs.builder().bucket(bucketName).object(objectKey).stream(
                  inputStream, file.getSize(), -1)
              .contentType(file.getContentType())
              .build());
      log.info("Successfully uploaded object '{}' to bucket '{}'", objectKey, bucketName);
    } catch (Exception e) {
      log.error(
          "Failed to upload object '{}' to bucket '{}': {}",
          objectKey,
          bucketName,
          e.getMessage(),
          e);
    }
  }

  @Override
  public InputStream download(String bucketName, String objectKey) {
    try {
      return minioClient.getObject(
          GetObjectArgs.builder().bucket(bucketName).object(objectKey).build());
    } catch (Exception e) {
      log.error(
          "Failed to download object '{}' from bucket '{}': {}",
          objectKey,
          bucketName,
          e.getMessage(),
          e);
      return null;
    }
  }

  @Override
  public boolean objectExists(String bucketName, String objectKey) {
    try {
      minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectKey).build());
      return true;
    } catch (Exception e) {
      log.warn(
          "Object '{}' does not exist in bucket '{}': {}", objectKey, bucketName, e.getMessage());
      return false;
    }
  }

  @Override
  public void deleteObject(String bucketName, String objectKey) {
    try {
      minioClient.removeObject(
          RemoveObjectArgs.builder().bucket(bucketName).object(objectKey).build());
      log.info("Successfully deleted object '{}' from bucket '{}'", objectKey, bucketName);
    } catch (Exception e) {
      log.error(
          "Failed to delete object '{}' from bucket '{}': {}",
          objectKey,
          bucketName,
          e.getMessage(),
          e);
    }
  }
}
