package com.rahim.storageservice.service;

import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

/**
 * @created 27/06/2025
 * @author Rahim Ahmed
 */
public interface StorageService {
  void createBucket(String bucketName);

  void deleteBucket(String bucketName);

  boolean bucketExists(String bucketName);

  String uploadImage(String bucketName, String id, MultipartFile file);

  InputStream download(String bucketName, String objectKey);

  String getPresignedUrl(String imageUrl);

  boolean objectExists(String bucketName, String objectKey);

  void deleteObject(String bucketName, String url);
}
