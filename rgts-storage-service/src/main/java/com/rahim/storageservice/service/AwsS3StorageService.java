package com.rahim.storageservice.service;

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
@Service("AWS_S3")
public class AwsS3StorageService implements StorageService {
  //  private final S3Client s3Client;

  @Override
  public void createBucket(String bucketName) {}

  @Override
  public void deleteBucket(String bucketName) {}

  @Override
  public boolean bucketExists(String bucketName) {
    return false;
  }

  @Override
  public String uploadImage(String bucketName, String objectKey, MultipartFile file) {
    return null;
  }

  @Override
  public InputStream download(String bucketName, String objectKey) {
    return null;
  }

  @Override
  public String getPresignedUrl(String imageUrl) {
    return null;
  }

  @Override
  public boolean objectExists(String bucketName, String objectKey) {
    return false;
  }

  @Override
  public void deleteObject(String bucketName, String url) {}
}
