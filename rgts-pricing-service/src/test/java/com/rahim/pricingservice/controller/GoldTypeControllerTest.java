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

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.MultipartFile;

class GoldTypeControllerTest extends BaseTestConfiguration {
  @Autowired private GoldTypeController goldTypeController;

  private MockMvc mockMvc;
  private MockMultipartFile mockImageFile;

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

    mockImageFile =
        new MockMultipartFile(
            "file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
  }

  @Test
  void shouldReturn201WithSuccessResponseWhenGoldTypeAddedSuccessfully() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("Necklace", "22K", BigDecimal.TEN, "g", "Test gold");

    MockMultipartFile jsonFile =
        new MockMultipartFile(
            "request", "", MediaType.APPLICATION_JSON_VALUE, requestToJson(request).getBytes());

    mockMvc
        .perform(multipart(Endpoints.GOLD_TYPE_ENDPOINT).file(jsonFile).file(mockImageFile))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value(request.getName()))
        .andExpect(jsonPath("$.purity").value(request.getPurity()))
        .andExpect(jsonPath("$.weight").value(request.getWeight() + " " + request.getUnit()))
        .andExpect(jsonPath("$.description").value(request.getDescription()));
  }

  @Test
  void shouldReturn400WhenGoldTypeRequestNameIsInvalid() throws Exception {
    AddGoldTypeRequest request = new AddGoldTypeRequest(null, "22K", BigDecimal.TEN, "g", "Valid");

    MockMultipartFile jsonFile =
        new MockMultipartFile(
            "request", "", MediaType.APPLICATION_JSON_VALUE, requestToJson(request).getBytes());

    mockMvc
        .perform(multipart(Endpoints.GOLD_TYPE_ENDPOINT).file(jsonFile).file(mockImageFile))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenGoldTypeCaratLabelIsInvalid() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("Invalid Carat", "30K", BigDecimal.TEN, "g", "Valid");

    MockMultipartFile jsonFile =
        new MockMultipartFile(
            "request", "", MediaType.APPLICATION_JSON_VALUE, requestToJson(request).getBytes());

    mockMvc
        .perform(multipart(Endpoints.GOLD_TYPE_ENDPOINT).file(jsonFile).file(mockImageFile))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenGoldTypeWeightIsInvalid() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("Invalid Weight", "30K", BigDecimal.valueOf(-10), "g", "Valid");

    MockMultipartFile jsonFile =
        new MockMultipartFile(
            "request", "", MediaType.APPLICATION_JSON_VALUE, requestToJson(request).getBytes());

    mockMvc
        .perform(multipart(Endpoints.GOLD_TYPE_ENDPOINT).file(jsonFile).file(mockImageFile))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenGoldTypeUnitIsInvalid() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("Invalid Unit", "30K", BigDecimal.TEN, "L", "Valid");

    MockMultipartFile jsonFile =
        new MockMultipartFile(
            "request", "", MediaType.APPLICATION_JSON_VALUE, requestToJson(request).getBytes());

    mockMvc
        .perform(multipart(Endpoints.GOLD_TYPE_ENDPOINT).file(jsonFile).file(mockImageFile))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenGoldTypeDescriptionIsNull() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("Invalid Description", "30K", BigDecimal.TEN, "L", null);

    MockMultipartFile jsonFile =
        new MockMultipartFile(
            "request", "", MediaType.APPLICATION_JSON_VALUE, requestToJson(request).getBytes());

    mockMvc
        .perform(multipart(Endpoints.GOLD_TYPE_ENDPOINT).file(jsonFile).file(mockImageFile))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn200AndInitialPageOfGoldTypes() throws Exception {
    AddGoldTypeRequest request1 =
        new AddGoldTypeRequest(
            "A Gold Necklace", "22K", BigDecimal.ONE, WeightUnit.GRAM.getValue(), "Desc");

    MockMultipartFile jsonFile1 =
        new MockMultipartFile(
            "request", "", MediaType.APPLICATION_JSON_VALUE, requestToJson(request1).getBytes());
    mockMvc.perform(multipart(Endpoints.GOLD_TYPE_ENDPOINT).file(jsonFile1).file(mockImageFile));

    AddGoldTypeRequest request2 =
        new AddGoldTypeRequest(
            "B Gold Necklace", "22K", BigDecimal.ONE, WeightUnit.GRAM.getValue(), "Desc");

    MockMultipartFile jsonFile2 =
        new MockMultipartFile(
            "request", "", MediaType.APPLICATION_JSON_VALUE, requestToJson(request2).getBytes());
    mockMvc.perform(multipart(Endpoints.GOLD_TYPE_ENDPOINT).file(jsonFile2).file(mockImageFile));

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

    MockMultipartFile jsonFile =
        new MockMultipartFile(
            "request", "", MediaType.APPLICATION_JSON_VALUE, requestToJson(request).getBytes());

    MvcResult postResult =
        mockMvc
            .perform(multipart(Endpoints.GOLD_TYPE_ENDPOINT).file(jsonFile).file(mockImageFile))
            .andExpect(status().isCreated())
            .andReturn();

    String responseContent = postResult.getResponse().getContentAsString();
    String id = JsonPath.read(responseContent, "$.id");

    UUID uuid = UUID.fromString(id);
    mockMvc
        .perform(get(Endpoints.GOLD_TYPE_ENDPOINT + "/{id}", uuid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(request.getName()))
        .andExpect(jsonPath("$.purity").value(request.getPurity()))
        .andExpect(jsonPath("$.description").value(request.getDescription()));
  }

  @Test
  void shouldReturn404AndGoldTypeOfValidID() throws Exception {
    mockMvc
        .perform(get(Endpoints.GOLD_TYPE_ENDPOINT + "/{id}", UUID.randomUUID()))
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

    MockMultipartFile jsonFile =
        new MockMultipartFile(
            "request", "", MediaType.APPLICATION_JSON_VALUE, requestToJson(request).getBytes());

    mockMvc.perform(multipart(Endpoints.GOLD_TYPE_ENDPOINT).file(jsonFile).file(mockImageFile));

    mockMvc
        .perform(get(Endpoints.GOLD_TYPE_ENDPOINT).param("name", goldName))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(goldName))
        .andExpect(jsonPath("$.purity").value(request.getPurity()))
        .andExpect(jsonPath("$.description").value(request.getDescription()));
  }

  @Test
  void shouldReturn200AndDeleteGoldTypeOfValidId() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest(
            "A Gold Necklace", "22K", BigDecimal.ONE, WeightUnit.GRAM.getValue(), "Desc");

    MockMultipartFile jsonFile =
        new MockMultipartFile(
            "request", "", MediaType.APPLICATION_JSON_VALUE, requestToJson(request).getBytes());

    MvcResult postResult =
        mockMvc
            .perform(multipart(Endpoints.GOLD_TYPE_ENDPOINT).file(jsonFile).file(mockImageFile))
            .andExpect(status().isCreated())
            .andReturn();

    String responseContent = postResult.getResponse().getContentAsString();
    String id = JsonPath.read(responseContent, "$.id");

    UUID uuid = UUID.fromString(id);
    mockMvc
        .perform(delete(Endpoints.GOLD_TYPE_ENDPOINT + "/{id}", uuid))
        .andExpect(status().isOk());
  }

  @Test
  void shouldReturn404WhenDeletingGoldTypeOfValidId() throws Exception {
    mockMvc
        .perform(delete(Endpoints.GOLD_TYPE_ENDPOINT + "/{id}", UUID.randomUUID()))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn400WhenImageFileIsEmpty() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("Necklace", "22K", BigDecimal.TEN, "g", "Test gold");

    MockMultipartFile jsonFile =
        new MockMultipartFile(
            "request", "", MediaType.APPLICATION_JSON_VALUE, requestToJson(request).getBytes());

    MockMultipartFile emptyFile =
        new MockMultipartFile("file", "", MediaType.IMAGE_JPEG_VALUE, new byte[0]);

    mockMvc
        .perform(multipart(Endpoints.GOLD_TYPE_ENDPOINT).file(jsonFile).file(emptyFile))
        .andExpect(status().isBadRequest());
  }
}
