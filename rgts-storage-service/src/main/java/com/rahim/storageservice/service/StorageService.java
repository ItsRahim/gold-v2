package com.rahim.storageservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @created 27/06/2025
 * @author Rahim Ahmed
 */
public interface StorageService {
  void createBucket(String bucketName);

  void deleteBucket(String bucketName);

  boolean bucketExists(String bucketName);

  void upload(String bucketName, String objectKey, MultipartFile file);

  InputStream download(String bucketName, String objectKey);

  boolean objectExists(String bucketName, String objectKey);

  void deleteObject(String bucketName, String objectKey);
}
