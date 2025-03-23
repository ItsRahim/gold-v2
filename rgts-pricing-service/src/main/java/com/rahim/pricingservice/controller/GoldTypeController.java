package com.rahim.pricingservice.controller;

import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@RestController
@RequestMapping("/api/v2/pricing-service/type")
@RequiredArgsConstructor
public class GoldTypeController {
    private static final Logger logger = LoggerFactory.getLogger(GoldTypeController.class);

    @Operation(summary = "Add a new gold type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gold type added successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Gold type already exists", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Error adding gold type", content = @Content(mediaType = "application/json"))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addGoldType(@Parameter(description = "Details of the gold type to be added", required = true) @RequestBody AddGoldTypeRequest request) {
        return ResponseEntity.ok(addGoldType(request));
    }
}
