package com.fitech.app.trainers.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO for creating reviews and ratings for trainer services")
public class CreateReviewDto {
    
    @NotNull(message = "El ID del contrato es requerido")
    @Schema(description = "Service contract ID being reviewed", example = "123", required = true)
    private Integer serviceContractId;
    
    @NotNull(message = "La calificación es requerida")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    @Schema(description = "Rating from 1 to 5 stars", example = "5", minimum = "1", maximum = "5", required = true)
    private Integer rating;
    
    @Size(max = 1000, message = "El comentario no puede exceder 1000 caracteres")
    @Schema(description = "Review comment", example = "Excelente entrenador, muy profesional y dedicado", maxLength = 1000)
    private String comment;
    
    @Schema(description = "Whether the review should be anonymous", example = "false")
    private Boolean isAnonymous = false;
} 