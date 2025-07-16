package com.fitech.app.users.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for detailed metric type and unit of measure association")
public class MetricTypeUOMDetailDto {
    @Schema(description = "Association ID", example = "1")
    private Integer id;
    
    @Schema(description = "Metric type ID", example = "1")
    private Integer metricTypeId;
    
    @Schema(description = "Metric type name", example = "Peso Corporal")
    private String metricTypeName;
    
    @Schema(description = "Unit of measure ID", example = "2")
    private Integer unitOfMeasureId;
    
    @Schema(description = "Unit of measure name", example = "Kilogramos")
    private String unitOfMeasureName;
    
    @Schema(description = "Unit symbol", example = "kg")
    private String unitOfMeasureSymbol;
    
    @Schema(description = "Unit description", example = "Unidad de medida para peso corporal")
    private String unitOfMeasureDescription;
    
    @Schema(description = "Whether this is the default unit for the metric type", example = "true")
    private boolean isDefault;
} 