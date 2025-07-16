package com.fitech.app.users.application.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO for fitness and health metric types")
public class MetricTypeDto {
    @Schema(description = "Metric type ID", example = "1")
    private Integer id;
    
    @Schema(description = "Metric type name", example = "Peso Corporal", allowableValues = {"Peso Corporal", "Altura", "IMC", "Grasa Corporal", "Masa Muscular"})
    private String name;
} 