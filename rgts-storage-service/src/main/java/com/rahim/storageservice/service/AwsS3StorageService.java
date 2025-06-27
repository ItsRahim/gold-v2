package com.rahim.storageservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.InputStream;

/**
 * @created 27/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3StorageService implements StorageService {
  private final S3Client s3Client;

  @Override
  public void createBucket(String bucketName) {}

  @Override
  public void deleteBucket(String bucketName) {}

  @Override
  public boolean bucketExists(String bucketName) {
    return false;
  }

  @Override
  public void upload(String bucketName, String objectKey, MultipartFile file) {}

  @Override
  public InputStream download(String bucketName, String objectKey) {
    return null;
  }

  @Override
  public boolean objectExists(String bucketName, String objectKey) {
    return false;
  }

  @Override
  public void deleteObject(String bucketName, String objectKey) {}
}
