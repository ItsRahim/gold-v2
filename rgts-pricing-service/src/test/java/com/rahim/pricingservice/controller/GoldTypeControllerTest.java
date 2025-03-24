package com.rahim.pricingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahim.common.exception.BadRequestException;
import com.rahim.pricingservice.BaseControllerTest;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.service.IAddGoldTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @MockitoBean
    private IAddGoldTypeService mockGoldTypeService;

    @Test
    public void shouldReturn200WhenGoldTypeAddedSuccessfully() throws Exception {
        AddGoldTypeRequest request = new AddGoldTypeRequest("GoldTypeName", "22K", BigDecimal.TEN, "Valid description");

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully added gold type"));

        verify(mockGoldTypeService, times(1)).addGoldType(request);
    }

    @Test
    public void shouldReturn400WhenGoldTypeAlreadyExists() throws Exception {
        AddGoldTypeRequest request = new AddGoldTypeRequest("ExistingGoldType", "22K", BigDecimal.TEN, "Description");

        doThrow(new BadRequestException("Gold type with name 'ExistingGoldType' already exists"))
                .when(mockGoldTypeService).addGoldType(request);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(mockGoldTypeService, times(1)).addGoldType(request);
    }

    @Test
    public void shouldReturn500WhenErrorOccursWhileAddingGoldType() throws Exception {
        AddGoldTypeRequest request = new AddGoldTypeRequest("GoldTypeName", "22K", BigDecimal.TEN, "Valid description");

        doThrow(new RuntimeException("Unexpected error")).when(mockGoldTypeService).addGoldType(request);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
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
