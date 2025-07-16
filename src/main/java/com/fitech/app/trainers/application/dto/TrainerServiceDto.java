package com.fitech.app.trainers.application.dto;

import com.fitech.app.trainers.domain.entities.TrainerService;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Schema(description = "DTO for trainer service information and details")
public class TrainerServiceDto {
    @Schema(description = "Service ID", example = "1")
    private Integer id;
    
    @Schema(description = "Trainer ID who owns the service", example = "123")
    private Integer trainerId;
    
    @Schema(description = "Service name", example = "Entrenamiento Personal Integral")
    private String name;
    
    @Schema(description = "Service description", example = "Entrenamiento personalizado de fuerza y resistencia")
    private String description;
    
    @Schema(description = "Total service price", example = "150.00")
    private BigDecimal totalPrice;
    
    @Schema(description = "Price per session", example = "30.00")
    private BigDecimal pricePerSession;
    
    @Schema(description = "Platform commission rate (as decimal)", example = "0.05", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal platformCommissionRate;
    
    @Schema(description = "Platform commission amount", example = "7.50", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal platformCommissionAmount;
    
    @Schema(description = "Trainer earnings after commission", example = "142.50", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal trainerEarnings;
    
    @Schema(description = "Whether service is in-person", example = "true")
    private Boolean isInPerson;
    
    @Schema(description = "Whether transport is included", example = "false")
    private Boolean transportIncluded;
    
    @Schema(description = "Transport cost per session", example = "15.00")
    private BigDecimal transportCostPerSession;
    
    @Schema(description = "Number of enrolled users", example = "5", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer enrolledUsersCount;
    
    @Schema(description = "Country where service is offered", example = "Perú")
    private String country;
    
    @Schema(description = "Whether service is active", example = "true")
    private Boolean isActive;
    
    @Schema(description = "Service creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
    
    @Schema(description = "List of districts where service is available")
    private List<ServiceDistrictDto> districts;

    public static TrainerServiceDto fromEntity(TrainerService entity) {
        TrainerServiceDto dto = new TrainerServiceDto();
        dto.setId(entity.getId());
        dto.setTrainerId(entity.getTrainerId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.setPricePerSession(entity.getPricePerSession());
        dto.setPlatformCommissionRate(entity.getPlatformCommissionRate());
        dto.setPlatformCommissionAmount(entity.getPlatformCommissionAmount());
        dto.setTrainerEarnings(entity.getTrainerEarnings());
        dto.setIsInPerson(entity.getIsInPerson());
        dto.setTransportIncluded(entity.getTransportIncluded());
        dto.setTransportCostPerSession(entity.getTransportCostPerSession());
        dto.setEnrolledUsersCount(entity.getEnrolledUsersCount());
        dto.setCountry(entity.getCountry());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        if (entity.getDistricts() != null) {
            dto.setDistricts(entity.getDistricts().stream()
                .map(ServiceDistrictDto::fromEntity)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
} 