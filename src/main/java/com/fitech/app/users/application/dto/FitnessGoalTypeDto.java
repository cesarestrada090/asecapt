package com.fitech.app.users.application.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO for fitness goal types and categories")
public class FitnessGoalTypeDto {
    @Schema(description = "Goal type ID", example = "1")
    private Integer id;
    
    @Schema(description = "Goal type name", example = "Pérdida de peso")
    private String name;
    
    @Schema(description = "Goal type description", example = "Objetivos relacionados con la reducción de peso corporal")
    private String description;
    
    @Schema(description = "Icon identifier for the goal type", example = "weight-loss-icon")
    private String icon;
} 