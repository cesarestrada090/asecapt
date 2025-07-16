package com.fitech.app.trainers.application.controllers;

import com.fitech.app.trainers.application.dto.TrainerClientDTO;
import com.fitech.app.trainers.application.services.TrainerClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/app/trainers")
@Tag(name = "Trainer-Client Relationships", description = "Operations for managing trainer-client relationships")
@SecurityRequirement(name = "bearerAuth")
public class TrainerClientController {

    @Autowired
    private TrainerClientService trainerClientService;

    @Operation(
        summary = "Get trainer's clients",
        description = "Retrieve paginated list of clients associated with a specific trainer, with filtering and sorting options"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clients retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "404", description = "Trainer not found",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{trainerId}/clients")
    public ResponseEntity<Page<TrainerClientDTO>> getTrainerClients(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long trainerId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "clientName") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(description = "Search term") @RequestParam(required = false) String search,
            @Parameter(description = "Contract status filter") @RequestParam(required = false) String status,
            @Parameter(description = "Service modality filter") @RequestParam(required = false) String modality) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<TrainerClientDTO> clients = trainerClientService.getTrainerClientsGrouped(
            trainerId, pageable, search, status, modality);
        
        return ResponseEntity.ok(clients);
    }
} 