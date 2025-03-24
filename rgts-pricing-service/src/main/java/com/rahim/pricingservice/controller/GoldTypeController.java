package com.rahim.pricingservice.controller;

import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.service.IAddGoldTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
@RequiredArgsConstructor
@RequestMapping("/api/v2/pricing-service/type")
@Tag(name = "Gold Type Management", description = "Endpoints for managing gold types")
public class GoldTypeController {
    private static final Logger logger = LoggerFactory.getLogger(GoldTypeController.class);
    private final IAddGoldTypeService goldTypeService;

    /**
     * Adds a new gold type to the system
     *
     * @param request Details of the gold type to be added
     * @return ResponseEntity with success message
     */
    @Operation(summary = "Add a new gold type", description = "Creates a new gold type with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gold type added successfully", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\": \"Successfully added gold type\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid gold type details or gold type already exists", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addGoldType(@Valid @Parameter(description = "Gold type details", required = true) @RequestBody AddGoldTypeRequest request) {
        goldTypeService.addGoldType(request);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully added gold type");
    }
}
