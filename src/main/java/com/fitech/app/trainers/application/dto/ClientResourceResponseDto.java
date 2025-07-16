package com.fitech.app.trainers.application.dto;

import com.fitech.app.trainers.domain.entities.ServiceResource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for client resource response with service and trainer information")
public class ClientResourceResponseDto {
    @Schema(description = "Resource ID", example = "1")
    private Integer id;
    
    @Schema(description = "Service ID", example = "123")
    private Integer serviceId;
    
    @Schema(description = "Service name", example = "Entrenamiento Personal")
    private String serviceName;
    
    @Schema(description = "Trainer name", example = "Carlos Trainer")
    private String trainerName;
    
    @Schema(description = "Client ID", example = "456")
    private Integer clientId;
    
    @Schema(description = "Client name", example = "Juan Pérez")
    private String clientName;
    
    @Schema(description = "Resource name", example = "Plan de Alimentación Semanal")
    private String resourceName;
    
    @Schema(description = "Type of resource", example = "DIET", allowableValues = {"DIET", "EXERCISE_ROUTINE", "TRAINING_PLAN"})
    private ServiceResource.ResourceType resourceType;
    
    @Schema(description = "Resource objective", example = "Reducir peso corporal en 2kg")
    private String resourceObjective;
    
    @Schema(description = "Detailed resource information", example = "Desayuno: Avena con frutas...")
    private String resourceDetails;
    
    @Schema(description = "Resource start date", example = "2024-01-15")
    private LocalDate startDate;
    
    @Schema(description = "Resource end date", example = "2024-02-15")
    private LocalDate endDate;
    
    @Schema(description = "Trainer notes", example = "Seguir estrictamente durante la primera semana")
    private String trainerNotes;
    
    @Schema(description = "Resource creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
    
    @Schema(description = "Whether the resource is active", example = "true")
    private Boolean isActive;
} 