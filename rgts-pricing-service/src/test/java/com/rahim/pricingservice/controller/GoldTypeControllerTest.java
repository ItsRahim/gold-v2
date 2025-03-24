package com.rahim.pricingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahim.common.exception.ApiExceptionHandler;
import com.rahim.pricingservice.BaseControllerTest;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.service.IAddGoldTypeService;
import com.rahim.pricingservice.service.IGoldTypeQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
public class GoldTypeControllerTest extends BaseControllerTest {

    private static final String ENDPOINT = "/api/v2/pricing-service/type";

    @Autowired
    private IAddGoldTypeService addGoldTypeService;

    @Autowired
    private IGoldTypeQueryService goldTypeQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        GoldTypeController goldTypeController = new GoldTypeController(addGoldTypeService, goldTypeQueryService);
        mockMvc = MockMvcBuilders.standaloneSetup(goldTypeController)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    public void shouldReturn200WhenGoldTypeAddedSuccessfully() throws Exception {
        AddGoldTypeRequest request = new AddGoldTypeRequest("GoldTypeName", "22K", BigDecimal.TEN, "Valid description");

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully added gold type"));
    }

    @Test
    public void shouldReturn400WhenGoldTypeAlreadyExists() throws Exception {
        AddGoldTypeRequest request = new AddGoldTypeRequest("ExistingGoldType", "22K", BigDecimal.TEN, "Description");
        AddGoldTypeRequest request1 = new AddGoldTypeRequest("ExistingGoldType", "22K", BigDecimal.TEN, "Description");

        addGoldTypeService.addGoldType(request);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400WhenGoldTypeRequestIsInvalid() throws Exception {
        AddGoldTypeRequest invalidRequest = new AddGoldTypeRequest(null, "22K", BigDecimal.TEN, "Valid description");

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}