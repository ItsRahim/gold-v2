package com.rahim.pricingservice.service;

import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
public interface IGoldTypeService {
  void addGoldType(AddGoldTypeRequest request, MultipartFile file);

  void deleteGoldTypeById(UUID id);
}
