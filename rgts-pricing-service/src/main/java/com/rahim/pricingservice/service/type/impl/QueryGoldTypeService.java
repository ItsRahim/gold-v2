package com.rahim.pricingservice.service.type.impl;

import com.rahim.common.response.AbstractResponseDTO;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.exception.GoldTypeNotFoundException;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import com.rahim.pricingservice.service.type.IQueryGoldTypeService;
import com.rahim.pricingservice.util.GoldResponseMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryGoldTypeService implements IQueryGoldTypeService {
  private final GoldTypeRepository goldTypeRepository;

  @Override
  public Page<AbstractResponseDTO> getAllGoldTypes(int page, int size) {
    Page<GoldType> goldTypes =
        goldTypeRepository.findAll(PageRequest.of(page, size, Sort.by("name")));
    return goldTypes.map(GoldResponseMapper::map);
  }

  @Override
  public GoldType getGoldTypeById(long id) {
    return goldTypeRepository
        .findById(id)
        .orElseThrow(
            () -> new GoldTypeNotFoundException("Gold Type with ID: " + id + " not found"));
  }

  @Override
  public List<GoldType> getAllGoldTypes() {
    return goldTypeRepository.findAll();
  }

  @Override
  public void saveGoldType(GoldType goldType) {
    goldTypeRepository.save(goldType);
  }
}
