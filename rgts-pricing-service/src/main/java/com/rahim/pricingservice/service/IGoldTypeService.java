package com.rahim.pricingservice.service;

import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
public interface IGoldTypeService {
  void addGoldType(AddGoldTypeRequest request, MultipartFile file);

  void deleteGoldTypeById(UUID id);
}
