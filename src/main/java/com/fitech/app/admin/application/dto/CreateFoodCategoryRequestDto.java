package com.fitech.app.admin.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateFoodCategoryRequestDto {
    
    @NotBlank(message = "El nombre de la categoría es requerido")
    private String name;
    
    private String description;
    
    private String icon;
    
    private Boolean isActive = true;
} 