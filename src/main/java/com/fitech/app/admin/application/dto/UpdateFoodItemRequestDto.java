package com.fitech.app.admin.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateFoodItemRequestDto {
    
    private String name;
    
    private String description;
    
    private Integer categoryId;
    
    private String imageUrl;
    
    @Min(value = 1, message = "El tamaño de porción debe ser mayor a 0")
    private Integer servingSizeGrams;
    
    @DecimalMin(value = "0.0", message = "Las calorías no pueden ser negativas")
    private BigDecimal caloriesPerServing;
    
    @DecimalMin(value = "0.0", message = "Las proteínas no pueden ser negativas")
    private BigDecimal proteinsPerServing;
    
    @DecimalMin(value = "0.0", message = "Los carbohidratos no pueden ser negativos")
    private BigDecimal carbohydratesPerServing;
    
    @DecimalMin(value = "0.0", message = "Las grasas no pueden ser negativas")
    private BigDecimal fatsPerServing;
    
    @DecimalMin(value = "0.0", message = "La fibra no puede ser negativa")
    private BigDecimal fiberPerServing;
    
    @DecimalMin(value = "0.0", message = "El azúcar no puede ser negativo")
    private BigDecimal sugarPerServing;
    
    @DecimalMin(value = "0.0", message = "El sodio no puede ser negativo")
    private BigDecimal sodiumPerServing;
    
    private Boolean isPopular;
    
    private Boolean isActive;
} 