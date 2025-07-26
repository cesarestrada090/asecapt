package com.asecapt.app.users.application.dto;

import com.asecapt.app.users.domain.entities.ServiceType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for service type categories and classifications")
public class ServiceTypeDTO {
    
    @Schema(description = "Service type ID", example = "1")
    private Integer id;
    
    @Schema(description = "Service type name", example = "Entrenamiento Personal")
    private String name;
    
    @Schema(description = "Service type description", example = "Servicios de entrenamiento personalizado uno a uno")
    private String description;
    
    @Schema(description = "Whether the service type is active", example = "true")
    private Boolean isActive;
    
    // Constructors
    public ServiceTypeDTO() {}
    
    public ServiceTypeDTO(Integer id, String name, String description, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
    }
    
    // Factory method para crear DTO desde entidad
    public static ServiceTypeDTO fromEntity(ServiceType serviceType) {
        return new ServiceTypeDTO(
            serviceType.getId(),
            serviceType.getName(),
            serviceType.getDescription(),
            serviceType.getIsActive()
        );
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
} 