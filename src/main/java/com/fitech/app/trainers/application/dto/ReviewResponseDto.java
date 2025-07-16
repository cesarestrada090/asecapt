package com.fitech.app.trainers.application.dto;

import lombok.Data;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "DTO for review response information")
public class ReviewResponseDto {
    @Schema(description = "Review ID", example = "1")
    private Integer id;
    
    @Schema(description = "Trainer ID who was reviewed", example = "123")
    private Integer trainerId;
    
    @Schema(description = "Trainer full name", example = "Carlos Trainer")
    private String trainerName;
    
    @Schema(description = "Trainer profile photo ID", example = "456")
    private Integer trainerProfilePhotoId;
    
    @Schema(description = "Client ID who made the review", example = "789")
    private Integer clientId;
    
    @Schema(description = "Client full name", example = "Juan Pérez")
    private String clientName;
    
    @Schema(description = "Service ID that was reviewed", example = "321")
    private Integer serviceId;
    
    @Schema(description = "Service name", example = "Entrenamiento Personal")
    private String serviceName;
    
    @Schema(description = "Service contract ID", example = "654")
    private Integer serviceContractId;
    
    @Schema(description = "Rating from 1 to 5", example = "5", minimum = "1", maximum = "5")
    private Integer rating;
    
    @Schema(description = "Review comment", example = "Excelente entrenador, muy profesional")
    private String comment;
    
    @Schema(description = "Whether the review is anonymous", example = "false")
    private Boolean isAnonymous;
    
    @Schema(description = "Trainer response to the review", example = "Gracias por tu comentario")
    private String trainerResponse;
    
    @Schema(description = "Trainer response timestamp")
    private LocalDateTime trainerResponseDate;
    
    @Schema(description = "Whether client can still edit the review", example = "true")
    private Boolean canEdit; // If client can still edit
    
    @Schema(description = "Review creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
} 