package com.rahim.pricingservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rahim.common.handler.ApiExceptionHandler;
import com.rahim.common.response.AbstractResponseDTO;
import com.rahim.pricingservice.BaseControllerTest;
import com.rahim.pricingservice.constant.Endpoints;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.dto.response.GoldTypeResponseDTO;
import com.rahim.pricingservice.enums.WeightUnit;
import com.rahim.pricingservice.service.type.IAddGoldTypeService;
import com.rahim.pricingservice.service.type.IQueryGoldTypeService;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class GoldTypeControllerTest extends BaseControllerTest {

  private MockMvc mockMvc;

  @Mock private IAddGoldTypeService addGoldTypeService;

  @Mock private IQueryGoldTypeService queryGoldTypeService;

  @InjectMocks private GoldTypeController goldTypeController;

  @BeforeEach
  public void setUp() {
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
  public void shouldReturn200WithSuccessResponseWhenGoldTypeAddedSuccessfully() throws Exception {
    AddGoldTypeRequest request =
        new AddGoldTypeRequest("Necklace", "22K", BigDecimal.TEN, "g", "Test gold");

    mockMvc
        .perform(
            post(Endpoints.GOLD_TYPE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestToJson(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("Successfully added gold type"));

    verify(addGoldTypeService, times(1)).addGoldType(any(AddGoldTypeRequest.class));
  }

  @Test
  public void shouldReturn400WhenGoldTypeRequestNameIsInvalid() throws Exception {
    AddGoldTypeRequest request = new AddGoldTypeRequest(null, "22K", BigDecimal.TEN, "g", "Valid");

    mockMvc
        .perform(
            post(Endpoints.GOLD_TYPE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestToJson(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturn200AndInitialPageOfGoldTypes() throws Exception {
    List<AbstractResponseDTO> mockGoldTypes =
        List.of(
            new GoldTypeResponseDTO(
                1, "1 Carat", "22K", BigDecimal.ONE, WeightUnit.GRAM, "Desc", BigDecimal.ONE),
            new GoldTypeResponseDTO(
                2, "10 Carat", "24K", BigDecimal.TEN, WeightUnit.GRAM, "Desc", BigDecimal.TEN));

    Page<AbstractResponseDTO> page =
        new PageImpl<>(mockGoldTypes, PageRequest.of(0, 10), mockGoldTypes.size());

    when(queryGoldTypeService.getAllGoldTypes(0, 10)).thenReturn(page);

    mockMvc
        .perform(get(Endpoints.GOLD_TYPE_ENDPOINT + "?page=0&size=10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.content[0].name").value("1 Carat"))
        .andExpect(jsonPath("$.content[1].name").value("10 Carat"));
  }
}
