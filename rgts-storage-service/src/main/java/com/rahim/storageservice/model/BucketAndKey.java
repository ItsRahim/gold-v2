package com.rahim.storageservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @created 27/06/2025
 * @author Rahim Ahmed
 */
@Getter
@Setter
@AllArgsConstructor
public class BucketAndKey {
    private final String bucketName;
    private final String objectKey;
}
