package com.fitech.app.trainers.application.dto;

import com.fitech.app.trainers.domain.entities.ServiceContract;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "DTO for detailed service contract information between trainers and clients")
public class ServiceContractDto {
    @Schema(description = "Contract ID", example = "1")
    private Integer id;
    
    @Schema(description = "Client ID who contracted the service", example = "123")
    private Integer clientId;
    
    @Schema(description = "Trainer ID providing the service", example = "456")
    private Integer trainerId;
    
    @Schema(description = "Service ID being contracted", example = "789")
    private Integer serviceId;
    
    @Schema(description = "Contract status", example = "ACTIVE", allowableValues = {"PENDING", "ACTIVE", "COMPLETED", "CANCELLED"})
    private String contractStatus;
    
    @Schema(description = "Contract start date", example = "2024-01-15")
    private LocalDate startDate;
    
    @Schema(description = "Contract end date", example = "2024-03-15")
    private LocalDate endDate;
    
    @Schema(description = "Contract completion timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime completionDate;
    
    @Schema(description = "Total contract amount", example = "300.00")
    private BigDecimal totalAmount;
    
    @Schema(description = "Payment status", example = "PAID", allowableValues = {"PENDING", "PAID", "PARTIAL", "REFUNDED"})
    private String paymentStatus;
    
    @Schema(description = "Terms acceptance timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime termsAcceptedAt;
    
    @Schema(description = "Contract notes", example = "Cliente requiere sesiones matutinas")
    private String notes;
    
    @Schema(description = "Contract creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
    
    // Información adicional del servicio y usuarios
    @Schema(description = "Service name", example = "Entrenamiento Personal Integral")
    private String serviceName;
    
    @Schema(description = "Client full name", example = "Juan Pérez")
    private String clientName;
    
    @Schema(description = "Client email", example = "juan.perez@email.com")
    private String clientEmail;
    
    @Schema(description = "Trainer full name", example = "Carlos Trainer")
    private String trainerName;
    
    // Información adicional del perfil del cliente
    @Schema(description = "Client first name", example = "Juan")
    private String clientFirstName;
    
    @Schema(description = "Client last name", example = "Pérez")
    private String clientLastName;
    
    @Schema(description = "Client profile photo ID", example = "456")
    private Integer clientProfilePhotoId;
    
    @Schema(description = "Client fitness goals", example = "[\"Pérdida de peso\", \"Ganancia muscular\"]")
    private List<String> clientFitnessGoals; // Objetivos de fitness del cliente
    
    // Información detallada del servicio
    @Schema(description = "Service description", example = "Entrenamiento personalizado de fuerza y resistencia")
    private String serviceDescription;
    
    @Schema(description = "Service price per session", example = "50.00")
    private BigDecimal servicePricePerSession;
    
    @Schema(description = "Whether service is in-person", example = "true")
    private Boolean serviceIsInPerson;
    
    @Schema(description = "Whether transport is included", example = "false")
    private Boolean serviceTransportIncluded;
    
    @Schema(description = "Transport cost per session", example = "15.00")
    private BigDecimal serviceTransportCostPerSession;
    
    @Schema(description = "Platform commission rate", example = "0.05", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal servicePlatformCommissionRate;
    
    @Schema(description = "Trainer earnings after commission", example = "285.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal serviceTrainerEarnings;
    
    @Schema(description = "Number of enrolled users in service", example = "3", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer serviceEnrolledUsersCount;
    
    @Schema(description = "Country where service is offered", example = "Perú")
    private String serviceCountry;

    public static ServiceContractDto fromEntity(ServiceContract entity) {
        ServiceContractDto dto = new ServiceContractDto();
        dto.setId(entity.getId());
        dto.setClientId(entity.getClientId());
        dto.setTrainerId(entity.getTrainerId());
        dto.setServiceId(entity.getServiceId());
        dto.setContractStatus(entity.getContractStatus().name());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setCompletionDate(entity.getCompletionDate());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setPaymentStatus(entity.getPaymentStatus().name());
        dto.setTermsAcceptedAt(entity.getTermsAcceptedAt());
        dto.setNotes(entity.getNotes());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        // Información adicional del servicio
        if (entity.getService() != null) {
            var service = entity.getService();
            dto.setServiceName(service.getName());
            dto.setServiceDescription(service.getDescription());
            dto.setServicePricePerSession(service.getPricePerSession());
            dto.setServiceIsInPerson(service.getIsInPerson());
            dto.setServiceTransportIncluded(service.getTransportIncluded());
            dto.setServiceTransportCostPerSession(service.getTransportCostPerSession());
            dto.setServicePlatformCommissionRate(service.getPlatformCommissionRate());
            dto.setServiceTrainerEarnings(service.getTrainerEarnings());
            dto.setServiceEnrolledUsersCount(service.getEnrolledUsersCount());
            dto.setServiceCountry(service.getCountry());
        }
        
        return dto;
    }
} 