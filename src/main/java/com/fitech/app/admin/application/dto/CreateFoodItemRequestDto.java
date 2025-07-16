package com.fitech.app.admin.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateFoodItemRequestDto {
    
    @NotBlank(message = "El nombre del alimento es requerido")
    private String name;
    
    private String description;
    
    @NotNull(message = "La categoría es requerida")
    private Integer categoryId;
    
    private String imageUrl;
    
    @NotNull(message = "El tamaño de porción es requerido")
    @Min(value = 1, message = "El tamaño de porción debe ser mayor a 0")
    private Integer servingSizeGrams;
    
    @NotNull(message = "Las calorías son requeridas")
    @DecimalMin(value = "0.0", message = "Las calorías no pueden ser negativas")
    private BigDecimal caloriesPerServing;
    
    @NotNull(message = "Las proteínas son requeridas")
    @DecimalMin(value = "0.0", message = "Las proteínas no pueden ser negativas")
    private BigDecimal proteinsPerServing;
    
    @NotNull(message = "Los carbohidratos son requeridos")
    @DecimalMin(value = "0.0", message = "Los carbohidratos no pueden ser negativos")
    private BigDecimal carbohydratesPerServing;
    
    @NotNull(message = "Las grasas son requeridas")
    @DecimalMin(value = "0.0", message = "Las grasas no pueden ser negativas")
    private BigDecimal fatsPerServing;
    
    @DecimalMin(value = "0.0", message = "La fibra no puede ser negativa")
    private BigDecimal fiberPerServing = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "El azúcar no puede ser negativo")
    private BigDecimal sugarPerServing = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "El sodio no puede ser negativo")
    private BigDecimal sodiumPerServing = BigDecimal.ZERO;
    
    private Boolean isPopular = false;
    
    private Boolean isActive = true;
} 