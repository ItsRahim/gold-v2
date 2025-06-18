package com.rahim.pricingservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
import com.rahim.common.handler.ApiExceptionHandler;
import com.rahim.pricingservice.BaseTestConfiguration;
import com.rahim.pricingservice.constant.Endpoints;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.service.IGoldTypeService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class GoldTypeControllerTest extends BaseTestConfiguration {
  @Autowired private IGoldTypeService goldTypeService;
  @Autowired private GoldTypeController goldTypeController;

  private MockMvc mockMvc;

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
        .andExpect(jsonPath("$.name").value(request.getName()))
        .andExpect(jsonPath("$.purity").value(request.getPurity()))
        .andExpect(jsonPath("$.weight").value(request.getWeight() + " " + request.getUnit()))
        .andExpect(jsonPath("$.description").value(request.getDescription()))
        .andExpect(jsonPath("$.price").value(11000.00));
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
    AddGoldTypeRequest request1 =
        new AddGoldTypeRequest(
            "A Gold Necklace", "22K", BigDecimal.ONE, WeightUnit.GRAM.getValue(), "Desc");
    goldTypeService.addGoldType(request1);

    AddGoldTypeRequest request2 =
        new AddGoldTypeRequest(
            "B Gold Necklace", "22K", BigDecimal.ONE, WeightUnit.GRAM.getValue(), "Desc");
    goldTypeService.addGoldType(request2);

    mockMvc
        .perform(get(Endpoints.GOLD_TYPE_ENDPOINT))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.content[0].name").value(request1.getName()))
        .andExpect(jsonPath("$.content[1].name").value(request2.getName()));
  }

  @Test
  void shouldReturn200AndGoldTypeOfValidID() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "A Gold Necklace", "22K", BigDecimal.ONE, WeightUnit.GRAM.getValue(), "Desc");

    MvcResult postResult =
        mockMvc
            .perform(
                post(Endpoints.GOLD_TYPE_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestToJson(request)))
            .andExpect(status().isOk())
            .andReturn();

    String responseContent = postResult.getResponse().getContentAsString();
    Integer id = JsonPath.read(responseContent, "$.id");

    mockMvc
        .perform(get(Endpoints.GOLD_TYPE_ENDPOINT + "/{id}", id.longValue()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(request.getName()))
        .andExpect(jsonPath("$.purity").value(request.getPurity()))
        .andExpect(jsonPath("$.description").value(request.getDescription()));
  }

  @Test
  void shouldReturn404AndGoldTypeOfValidID() throws Exception {
    mockMvc
        .perform(get(Endpoints.GOLD_TYPE_ENDPOINT + "/{id}", 100))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn200AndTypeOfValidName() throws Exception {
    String goldName = "Sovereign - Victoria, Old Veiled Head";

    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            goldName,
            "22K",
            BigDecimal.ONE,
            WeightUnit.GRAM.getValue(),
            "Historic British gold coin");

    goldTypeService.addGoldType(request);

    mockMvc
        .perform(get(Endpoints.GOLD_TYPE_ENDPOINT).param("name", goldName))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(goldName))
        .andExpect(jsonPath("$.purity").value(request.getPurity()))
        .andExpect(jsonPath("$.description").value(request.getDescription()));
  }
}
