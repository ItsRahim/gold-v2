package com.rahim.pricingservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rahim.common.handler.ApiExceptionHandler;
import com.rahim.pricingservice.BaseControllerTest;
import com.rahim.pricingservice.constant.Endpoints;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.service.type.IAddGoldTypeService;
import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class GoldTypeControllerTest extends BaseControllerTest {

  private MockMvc mockMvc;

  @Autowired private IAddGoldTypeService addGoldTypeService;

  @Autowired private GoldTypeController goldTypeController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();

    mockMvc =
        MockMvcBuilders.standaloneSetup(goldTypeController)
            .setValidator(validator)
            .setControllerAdvice(new ApiExceptionHandler())
            .build();
  }

  @Test
  void shouldReturn200WithSuccessResponseWhenGoldTypeAddedSuccessfully() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("Necklace", "22K", BigDecimal.TEN, "g", "Test gold");

    mockMvc
        .perform(
            post(Endpoints.GOLD_TYPE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestToJson(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Necklace"))
        .andExpect(jsonPath("$.purity").value("22K"))
        .andExpect(jsonPath("$.weight").value("10 g"))
        .andExpect(jsonPath("$.description").value("Test gold"))
        .andExpect(jsonPath("$.price").value(730.60));
  }

  @Test
  void shouldReturn400WhenGoldTypeRequestNameIsInvalid() throws Exception {
    AddGoldTypeRequest request = new AddGoldTypeRequest(null, "22K", BigDecimal.TEN, "g", "Valid");

    mockMvc
        .perform(
            post(Endpoints.GOLD_TYPE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestToJson(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenGoldTypeCaratLabelIsInvalid() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("Invalid Carat", "30K", BigDecimal.TEN, "g", "Valid");

    mockMvc
        .perform(
            post(Endpoints.GOLD_TYPE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestToJson(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenGoldTypeWeightIsInvalid() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("Invalid Weight", "30K", BigDecimal.valueOf(-10), "g", "Valid");

    mockMvc
        .perform(
            post(Endpoints.GOLD_TYPE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestToJson(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenGoldTypeUnitIsInvalid() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("Invalid Unit", "30K", BigDecimal.TEN, "L", "Valid");

    mockMvc
        .perform(
            post(Endpoints.GOLD_TYPE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestToJson(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenGoldTypeDescriptionIsNull() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("Invalid Description", "30K", BigDecimal.TEN, "L", null);

    mockMvc
        .perform(
            post(Endpoints.GOLD_TYPE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestToJson(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn200AndInitialPageOfGoldTypes() throws Exception {
    addGoldTypeService.addGoldType(
        new AddGoldTypeRequest(
            "1 Carat", "22K", BigDecimal.ONE, WeightUnit.GRAM.getValue(), "Desc"));
    addGoldTypeService.addGoldType(
        new AddGoldTypeRequest(
            "10 Carat", "22K", BigDecimal.ONE, WeightUnit.GRAM.getValue(), "Desc"));

    mockMvc
        .perform(get(Endpoints.GOLD_TYPE_ENDPOINT))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.content[0].name").value("1 Carat"))
        .andExpect(jsonPath("$.content[1].name").value("10 Carat"));
  }
}
