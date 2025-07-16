package com.fitech.app.trainers.application.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "DTO for creating new trainer services")
public class CreateTrainerServiceDto {
    
    @NotNull(message = "El tipo de servicio es requerido")
    @Schema(description = "Service type ID", example = "1", required = true)
    private Integer serviceTypeId;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @Schema(description = "Service description", example = "Entrenamiento personalizado de fuerza y resistencia", maxLength = 1000)
    private String description;
    
    @NotNull(message = "El precio total es requerido")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 dígitos enteros y 2 decimales")
    @Schema(description = "Total service price", example = "150.00", required = true, minimum = "0.01")
    private BigDecimal totalPrice;
    
    @NotNull(message = "Debe especificar si el servicio es presencial")
    @Schema(description = "Whether the service is in-person", example = "true", required = true)
    private Boolean isInPerson;
    
    @Schema(description = "Whether transport is included in the service", example = "false")
    private Boolean transportIncluded = false;
    
    @DecimalMin(value = "0.00", message = "El costo de transporte no puede ser negativo")
    @Digits(integer = 6, fraction = 2, message = "El costo de transporte debe tener máximo 6 dígitos enteros y 2 decimales")
    @Schema(description = "Transport cost per session", example = "15.00", minimum = "0.00")
    private BigDecimal transportCostPerSession = BigDecimal.ZERO;
    
    @Schema(description = "Country where service is offered", example = "Perú")
    private String country = "Perú";
    
    // Lista de distritos para servicios presenciales
    @Schema(description = "List of districts for in-person services", example = "[\"Miraflores\", \"San Isidro\", \"Barranco\"]")
    private List<String> districts;
} 