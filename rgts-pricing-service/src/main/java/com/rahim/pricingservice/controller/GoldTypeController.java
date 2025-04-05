package com.rahim.pricingservice.controller;

import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.repository.GoldTypeRepository;
import com.rahim.pricingservice.service.IAddGoldTypeService;
import com.rahim.pricingservice.service.IQueryGoldTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  private final IAddGoldTypeService addGoldTypeService;
  private final IQueryGoldTypeService queryGoldTypeService;

  @Operation(
      summary = "Add a new gold type",
      description = "Creates a new gold type with the provided details")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Gold type added successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"message\": \"Successfully added gold type\"}"))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid gold type details or gold type already exists",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json"))
      })
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> addGoldType(
      @Valid @Parameter(description = "Gold type details", required = true) @RequestBody
          AddGoldTypeRequest request) {
    addGoldTypeService.addGoldType(request);
    return ResponseEntity.status(HttpStatus.OK).body("Successfully added gold type");
  }

  @Operation(
      summary = "Retrieve paginated list of gold types",
      description =
          "Returns a paginated list of all available gold types in the system. Use `page` and `size` query parameters to control pagination.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Gold types retrieved successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GoldType.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<GoldType>> getAllGoldTypes(
      @Parameter(description = "Page number (0-based)", example = "0")
          @RequestParam(value = "page", defaultValue = "0")
          int page,
      @Parameter(description = "Number of items per page", example = "10")
          @RequestParam(value = "size", defaultValue = "10")
          int size) {

    logger.info("Received request to get all available gold types");
    return ResponseEntity.ok(queryGoldTypeService.getAllGoldTypes(page, size));
  }
}
