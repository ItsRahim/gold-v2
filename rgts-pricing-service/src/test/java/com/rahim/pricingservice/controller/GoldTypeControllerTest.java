package com.rahim.pricingservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahim.common.exception.ApiExceptionHandler;
import com.rahim.pricingservice.BaseControllerTest;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import com.rahim.pricingservice.service.IAddGoldTypeService;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
public class GoldTypeControllerTest extends BaseControllerTest {

  private static final String ENDPOINT = "/api/v2/pricing-service/type";

  @Autowired private IAddGoldTypeService addGoldTypeService;

  @Autowired private GoldTypeRepository goldTypeRepository;

  @Autowired private ObjectMapper objectMapper;

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    GoldTypeController goldTypeController =
        new GoldTypeController(addGoldTypeService, goldTypeRepository);
    mockMvc =
        MockMvcBuilders.standaloneSetup(goldTypeController)
            .setControllerAdvice(new ApiExceptionHandler())
            .build();
  }

  @Test
  public void shouldReturn200WhenGoldTypeAddedSuccessfully() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("GoldTypeName", "22K", BigDecimal.TEN, "Valid description");

    mockMvc
        .perform(
            post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Successfully added gold type"));
  }

  @Test
  public void shouldReturn400WhenGoldTypeAlreadyExists() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("ExistingGoldType", "22K", BigDecimal.TEN, "Description");
    AddGoldTypeRequest request1 =
        new AddGoldTypeRequest("ExistingGoldType", "22K", BigDecimal.TEN, "Description");

    addGoldTypeService.addGoldType(request);

    mockMvc
        .perform(
            post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturn400WhenGoldTypeRequestIsInvalid() throws Exception {
    AddGoldTypeRequest invalidRequest =
        new AddGoldTypeRequest(null, "22K", BigDecimal.TEN, "Valid description");

    mockMvc
        .perform(
            post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturn200AndListOfGoldTypes() throws Exception {
    mockMvc
        .perform(get(ENDPOINT).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(24)))
        .andExpect(jsonPath("$[0].name").value("24 Carat"))
        .andExpect(jsonPath("$[1].name").value("23 Carat"));

    List<GoldType> goldTypes =
        List.of(
            new GoldType("GoldType1", "22K", BigDecimal.TEN, "Description1"),
            new GoldType("GoldType2", "24K", BigDecimal.valueOf(15), "Description2"));

    goldTypeRepository.saveAll(goldTypes);

    mockMvc
        .perform(get(ENDPOINT).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(26)))
        .andExpect(jsonPath("$[24].name").value("GoldType1"))
        .andExpect(jsonPath("$[25].name").value("GoldType2"));
  }
}
