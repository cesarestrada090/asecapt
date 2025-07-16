package com.fitech.app.users.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for associating metric types with units of measure")
public class MetricTypeUOMDto {
    @Schema(description = "Association ID", example = "1")
    private Integer id;
    
    @NotNull(message = "El tipo de métrica es requerido")
    @Schema(description = "Metric type ID", example = "1", required = true)
    private Integer metricTypeId;
    
    @NotNull(message = "La unidad de medida es requerida")
    @Schema(description = "Unit of measure ID", example = "2", required = true)
    private Integer unitOfMeasureId;
    
    @Schema(description = "Whether this is the default unit for the metric type", example = "true")
    private boolean isDefault;
} 