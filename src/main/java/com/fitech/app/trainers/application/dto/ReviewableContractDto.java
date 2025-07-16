package com.fitech.app.trainers.application.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@Schema(description = "DTO for contracts that can be reviewed by clients")
public class ReviewableContractDto {
    @Schema(description = "Contract ID", example = "123")
    private Integer contractId;
    
    @Schema(description = "Trainer ID", example = "456")
    private Integer trainerId;
    
    @Schema(description = "Trainer full name", example = "Carlos Trainer")
    private String trainerName;
    
    @Schema(description = "Trainer profile photo ID", example = "789")
    private Integer trainerProfilePhotoId;
    
    @Schema(description = "Service ID", example = "321")
    private Integer serviceId;
    
    @Schema(description = "Service name", example = "Entrenamiento Personal")
    private String serviceName;
    
    @Schema(description = "Service description", example = "Entrenamiento personalizado de fuerza")
    private String serviceDescription;
    
    @Schema(description = "Contract status", example = "COMPLETED", allowableValues = {"PENDING", "ACTIVE", "COMPLETED", "CANCELLED"})
    private String contractStatus;
    
    @Schema(description = "Contract completion timestamp")
    private LocalDateTime completionDate;
    
    @Schema(description = "Contract end date")
    private LocalDateTime endDate;
    
    @Schema(description = "Whether client has already reviewed this contract", example = "false")
    private Boolean hasReview; // If client already reviewed this contract
    
    @Schema(description = "ID of existing review if any", example = "567")
    private Integer existingReviewId; // ID of existing review if any
    
    // Constructor for JPA projection - order must match exactly the SELECT clause
    public ReviewableContractDto(Integer contractId, Integer trainerId, String trainerName, 
                                Integer trainerProfilePhotoId, Integer serviceId, String serviceName, 
                                String serviceDescription, String contractStatus, 
                                LocalDateTime completionDate, LocalDateTime endDate, 
                                Boolean hasReview, Integer existingReviewId) {
        this.contractId = contractId;
        this.trainerId = trainerId;
        this.trainerName = trainerName;
        this.trainerProfilePhotoId = trainerProfilePhotoId;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.contractStatus = contractStatus;
        this.completionDate = completionDate;
        this.endDate = endDate;
        this.hasReview = hasReview;
        this.existingReviewId = existingReviewId;
    }
} 