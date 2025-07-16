package com.fitech.app.users.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO for trainer search filters and criteria")
public class TrainerSearchDto {
    @Schema(description = "List of fitness goal IDs to filter trainers", example = "[1, 3, 5]")
    private List<Integer> fitnessGoalIds;
    
    @Schema(description = "Search query for trainer name", example = "Juan entrenador")
    private String query; // Para búsqueda por nombre
    
    @Schema(description = "Location filter for trainers", example = "Miraflores")
    private String location; // Para futuras implementaciones
    
    @Schema(description = "Minimum rating filter", example = "4.5", minimum = "0.0", maximum = "5.0")
    private Double minRating; // Para futuras implementaciones
    
    @Schema(description = "Maximum price filter", example = "200.00", minimum = "0.0")
    private Double maxPrice; // Para futuras implementaciones
} 