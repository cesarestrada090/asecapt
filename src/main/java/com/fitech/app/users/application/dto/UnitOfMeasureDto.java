package com.fitech.app.users.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for units of measure used in fitness metrics")
public class UnitOfMeasureDto {
    @Schema(description = "Unit of measure ID", example = "1")
    private Integer id;
    
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 50, message = "El nombre no puede exceder los 50 caracteres")
    @Schema(description = "Unit name", example = "Kilogramos", maxLength = 50, required = true)
    private String name;
    
    @NotBlank(message = "El símbolo es requerido")
    @Size(max = 10, message = "El símbolo no puede exceder los 10 caracteres")
    @Schema(description = "Unit symbol", example = "kg", maxLength = 10, required = true)
    private String symbol;
    
    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    @Schema(description = "Unit description", example = "Unidad de medida para peso corporal", maxLength = 255)
    private String description;
} 