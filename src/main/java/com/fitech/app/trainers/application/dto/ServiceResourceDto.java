package com.fitech.app.trainers.application.dto;

import com.fitech.app.trainers.domain.entities.ServiceResource;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "DTO for service resources like diets and exercise routines")
public class ServiceResourceDto {
    @Schema(description = "Resource ID", example = "1")
    private Integer id;
    
    @Schema(description = "Service ID this resource belongs to", example = "123")
    private Integer serviceId;
    
    @Schema(description = "Client ID this resource is for", example = "456")
    private Integer clientId;
    
    @Schema(description = "Trainer ID who created the resource", example = "789")
    private Integer trainerId;
    
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
    
    @Schema(description = "Trainer notes about the resource", example = "Seguir estrictamente durante la primera semana")
    private String trainerNotes;
    
    @Schema(description = "Whether the resource is active", example = "true")
    private Boolean isActive;
    
    @Schema(description = "Resource creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    // Constructor para crear desde entidad
    public ServiceResourceDto(ServiceResource resource) {
        this.id = resource.getId();
        this.serviceId = resource.getServiceId();
        this.clientId = resource.getClientId();
        this.trainerId = resource.getTrainerId();
        this.resourceName = resource.getResourceName();
        this.resourceType = resource.getResourceType();
        this.resourceObjective = resource.getResourceObjective();
        this.resourceDetails = resource.getResourceDetails();
        this.startDate = resource.getStartDate();
        this.endDate = resource.getEndDate();
        this.trainerNotes = resource.getTrainerNotes();
        this.isActive = resource.getIsActive();
        this.createdAt = resource.getCreatedAt();
        this.updatedAt = resource.getUpdatedAt();
    }

    // Constructor vacío para desserialización
    public ServiceResourceDto() {}

    // Método helper para convertir a entidad
    public ServiceResource toEntity() {
        ServiceResource resource = new ServiceResource();
        resource.setId(this.id);
        resource.setServiceId(this.serviceId);
        resource.setClientId(this.clientId);
        resource.setTrainerId(this.trainerId);
        resource.setResourceName(this.resourceName);
        resource.setResourceType(this.resourceType);
        resource.setResourceObjective(this.resourceObjective);
        resource.setResourceDetails(this.resourceDetails);
        resource.setStartDate(this.startDate);
        resource.setEndDate(this.endDate);
        resource.setTrainerNotes(this.trainerNotes);
        resource.setIsActive(this.isActive);
        return resource;
    }
} 