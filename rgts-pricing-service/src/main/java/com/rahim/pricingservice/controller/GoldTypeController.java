package com.rahim.pricingservice.controller;

import com.rahim.pricingservice.constant.Endpoints;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.dto.response.GoldTypeResponseDTO;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.service.type.IGoldTypeService;
import com.rahim.pricingservice.service.type.IQueryGoldTypeService;
import com.rahim.pricingservice.util.GoldResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoints.GOLD_TYPE_ENDPOINT)
@Tag(name = "Gold Type Management", description = "Endpoints for managing gold types")
public class GoldTypeController {
  private final IGoldTypeService goldTypeService;
  private final IQueryGoldTypeService queryGoldTypeService;

  @Operation(
      summary = "Add a new gold type",
      description = "Creates a new gold type with the provided details")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Gold type added successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid gold type details or gold type already exists",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GoldTypeResponseDTO> addGoldType(
      @Valid @Parameter(description = "Gold type details", required = true) @RequestBody
          AddGoldTypeRequest request) {
    goldTypeService.addGoldType(request);
    GoldTypeResponseDTO response = queryGoldTypeService.getGoldTypeByName(request.getName());
    return ResponseEntity.status(HttpStatus.OK).body(response);
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
  public ResponseEntity<Page<GoldTypeResponseDTO>> getAllGoldTypes(
      @Parameter(description = "Page number (0-based)", example = "0")
          @RequestParam(value = "page", defaultValue = "0")
          int page,
      @Parameter(description = "Number of items per page", example = "10")
          @RequestParam(value = "size", defaultValue = "10")
          int size) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(queryGoldTypeService.getAllGoldTypes(page, size));
  }

  @Operation(
      summary = "Retrieve a gold type by ID",
      description = "Returns the details of a specific gold type by its unique identifier.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Gold type retrieved successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GoldTypeResponseDTO.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Gold type not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GoldTypeResponseDTO> getGoldTypeById(
      @Parameter(description = "Unique identifier of the gold type", example = "1")
          @PathVariable("id")
          int id) {
    GoldType goldType = queryGoldTypeService.getGoldTypeById(id);
    GoldTypeResponseDTO response = GoldResponseMapper.mapToGoldType(goldType);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Operation(
      summary = "Retrieve gold type by name",
      description = "Returns the details of a specific gold type by its unique name.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Gold type retrieved successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GoldTypeResponseDTO.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Gold type not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @GetMapping(params = "name", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GoldTypeResponseDTO> getGoldTypeByName(
      @Parameter(description = "The name of the gold type", example = "Gold Sovereign")
          @RequestParam("name")
          String name) {
    return ResponseEntity.status(HttpStatus.OK).body(queryGoldTypeService.getGoldTypeByName(name));
  }

  @Operation(
      summary = "Delete a gold type",
      description = "Deletes a gold type by its unique identifier")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Gold type deleted successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid gold type details",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "404",
            description = "Gold type not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> deleteGoldType(
      @Valid @Parameter(description = "Gold type details", required = true) @RequestParam int id) {
    goldTypeService.deleteGoldTypeById(id);
    return ResponseEntity.status(HttpStatus.OK)
        .body("Successfully deleted gold type with ID: " + id);
  }
}
