package com.fitech.app.users.application.controllers;

import com.fitech.app.users.application.dto.ClientProfileDTO;
import com.fitech.app.users.domain.services.ClientProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/app/clients")
@CrossOrigin(origins = "*")
@Tag(name = "Client Profile", description = "Client profile management operations")
@SecurityRequirement(name = "bearerAuth")
public class ClientProfileController {

    @Autowired
    private ClientProfileService clientProfileService;

    @Operation(
        summary = "Get client profile",
        description = "Retrieve detailed profile information for a specific client"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Client profile retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientProfileDTO.class))),
        @ApiResponse(responseCode = "404", description = "Client not found",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{clientId}/profile")
    public ResponseEntity<ClientProfileDTO> getClientProfile(
            @Parameter(description = "Client ID", required = true) @PathVariable Long clientId) {
        try {
            ClientProfileDTO profile = clientProfileService.getClientProfile(clientId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
} 