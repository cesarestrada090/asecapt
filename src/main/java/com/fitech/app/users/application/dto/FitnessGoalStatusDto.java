package com.fitech.app.users.application.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO for fitness goal status tracking")
public class FitnessGoalStatusDto {
    @Schema(description = "Goal status ID", example = "1")
    private Integer id;
    
    @Schema(description = "Status name", example = "EN_PROGRESO", allowableValues = {"PENDIENTE", "EN_PROGRESO", "COMPLETADO", "PAUSADO"})
    private String name;
} 