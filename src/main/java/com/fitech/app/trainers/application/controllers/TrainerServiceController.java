package com.fitech.app.trainers.application.controllers;

import com.fitech.app.trainers.application.dto.CreateTrainerServiceDto;
import com.fitech.app.trainers.application.dto.TrainerServiceDto;
import com.fitech.app.trainers.domain.services.TrainerServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/app/trainer-services")
@Validated
@Tag(name = "Trainer Services", description = "Operations for managing trainer services and offerings")
@SecurityRequirement(name = "bearerAuth")
public class TrainerServiceController {

    @Autowired
    private TrainerServiceService trainerServiceService;

    @Operation(
        summary = "Create a new trainer service",
        description = "Create a new service offering for a specific trainer"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Service created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainerServiceDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid service data",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/{trainerId}")
    public ResponseEntity<TrainerServiceDto> createService(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Integer trainerId,
            @Valid @RequestBody CreateTrainerServiceDto createDto) {
        try {
            log.info("Creating service for trainer ID: {}", trainerId);
            TrainerServiceDto service = trainerServiceService.createService(trainerId, createDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(service);
        } catch (Exception e) {
            log.error("Error creating service for trainer ID: {}", trainerId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{serviceId}/trainer/{trainerId}")
    public ResponseEntity<TrainerServiceDto> updateService(
            @PathVariable Integer serviceId,
            @PathVariable Integer trainerId,
            @Valid @RequestBody CreateTrainerServiceDto updateDto) {
        try {
            log.info("Updating service ID: {} for trainer ID: {}", serviceId, trainerId);
            TrainerServiceDto service = trainerServiceService.updateService(serviceId, trainerId, updateDto);
            return ResponseEntity.ok(service);
        } catch (RuntimeException e) {
            log.error("Error updating service ID: {} for trainer ID: {}", serviceId, trainerId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error updating service ID: {} for trainer ID: {}", serviceId, trainerId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<TrainerServiceDto> getService(@PathVariable Integer serviceId) {
        try {
            TrainerServiceDto service = trainerServiceService.getServiceById(serviceId);
            return ResponseEntity.ok(service);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<TrainerServiceDto>> getServicesByTrainer(@PathVariable Integer trainerId) {
        List<TrainerServiceDto> services = trainerServiceService.getServicesByTrainer(trainerId);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/trainer/{trainerId}/active")
    public ResponseEntity<List<TrainerServiceDto>> getActiveServicesByTrainer(@PathVariable Integer trainerId) {
        List<TrainerServiceDto> services = trainerServiceService.getActiveServicesByTrainer(trainerId);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/active")
    public ResponseEntity<List<TrainerServiceDto>> getAllActiveServices() {
        List<TrainerServiceDto> services = trainerServiceService.getAllActiveServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/by-type")
    public ResponseEntity<List<TrainerServiceDto>> getServicesByType(@RequestParam Boolean isInPerson) {
        List<TrainerServiceDto> services = trainerServiceService.getServicesByType(isInPerson);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/by-district")
    public ResponseEntity<List<TrainerServiceDto>> getServicesByDistrict(@RequestParam String district) {
        List<TrainerServiceDto> services = trainerServiceService.getServicesByDistrict(district);
        return ResponseEntity.ok(services);
    }

    @DeleteMapping("/{serviceId}/trainer/{trainerId}")
    public ResponseEntity<Void> deleteService(
            @PathVariable Integer serviceId,
            @PathVariable Integer trainerId) {
        try {
            trainerServiceService.deleteService(serviceId, trainerId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting service ID: {} for trainer ID: {}", serviceId, trainerId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/{serviceId}/trainer/{trainerId}/deactivate")
    public ResponseEntity<Void> deactivateService(
            @PathVariable Integer serviceId,
            @PathVariable Integer trainerId) {
        try {
            trainerServiceService.deactivateService(serviceId, trainerId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error deactivating service ID: {} for trainer ID: {}", serviceId, trainerId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/{serviceId}/trainer/{trainerId}/activate")
    public ResponseEntity<Void> activateService(
            @PathVariable Integer serviceId,
            @PathVariable Integer trainerId) {
        try {
            trainerServiceService.activateService(serviceId, trainerId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error activating service ID: {} for trainer ID: {}", serviceId, trainerId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/trainer/{trainerId}/count")
    public ResponseEntity<Long> countActiveServicesByTrainer(@PathVariable Integer trainerId) {
        Long count = trainerServiceService.countActiveServicesByTrainer(trainerId);
        return ResponseEntity.ok(count);
    }
} 