package com.rahim.pricingservice.controller;

import com.rahim.pricingservice.constant.Endpoints;
import com.rahim.pricingservice.dto.request.AddGoldTypeRequest;
import com.rahim.pricingservice.dto.response.GoldTypeResponseDTO;
import com.rahim.pricingservice.entity.GoldType;
import com.rahim.pricingservice.service.IGoldTypeService;
import com.rahim.pricingservice.service.IQueryGoldTypeService;
import com.rahim.pricingservice.util.GoldResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
  private final GoldResponseMapper goldResponseMapper;

  @Operation(
      summary = "Add a new gold type",
      description = "Creates a new gold type with the provided details and optional image file")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Gold type created successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or duplicate gold type",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  @PostMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GoldTypeResponseDTO> addGoldType(
      @Parameter(description = "Gold type details (JSON)", required = true) @RequestPart("request")
          AddGoldTypeRequest request,
      @Parameter(description = "Image file for the gold type") @RequestPart(value = "file")
          MultipartFile file) {
    goldTypeService.addGoldType(request, file);

    GoldTypeResponseDTO response = queryGoldTypeService.getGoldTypeByName(request.getName());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
          UUID id) {
    GoldType goldType = queryGoldTypeService.getGoldTypeById(id);
    GoldTypeResponseDTO response = goldResponseMapper.mapToGoldType(goldType);
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
  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> deleteGoldType(
      @Parameter(description = "Gold type unique identifier", required = true)
          @PathVariable(name = "id")
          UUID id) {
    goldTypeService.deleteGoldTypeById(id);
    return ResponseEntity.status(HttpStatus.OK)
        .body("Gold type with ID " + id + " deleted successfully.");
  }
}
