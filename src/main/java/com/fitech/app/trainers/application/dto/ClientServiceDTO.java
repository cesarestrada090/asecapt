package com.fitech.app.trainers.application.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for client service information from trainer's perspective")
public class ClientServiceDTO {
    @Schema(description = "Service ID", example = "1")
    private Long serviceId;
    
    @Schema(description = "Service name", example = "Entrenamiento Personal Integral")
    private String serviceName;
    
    @Schema(description = "Service description", example = "Entrenamiento personalizado de fuerza y resistencia")
    private String serviceDescription;
    
    @Schema(description = "Price per session", example = "50.00")
    private BigDecimal servicePricePerSession;
    
    @Schema(description = "Service type", example = "Entrenamiento Personal")
    private String serviceType;
    
    @Schema(description = "Service modality", example = "PRESENCIAL", allowableValues = {"PRESENCIAL", "VIRTUAL", "HIBRIDO"})
    private String modality;
    
    @Schema(description = "Whether transport is included", example = "false")
    private Boolean transportIncluded;
    
    @Schema(description = "Transport cost", example = "15.00")
    private BigDecimal transportCost;
    
    @Schema(description = "Service status", example = "ACTIVE", allowableValues = {"PENDING", "ACTIVE", "COMPLETED", "CANCELLED"})
    private String status;
    
    @Schema(description = "Service start date", example = "2024-01-15")
    private String startDate;
    
    @Schema(description = "Service end date", example = "2024-03-15")
    private String endDate;
    
    @Schema(description = "Total amount paid for this service", example = "300.00")
    private BigDecimal totalPaid;
    
    @Schema(description = "Contract ID for this service", example = "123")
    private Long contractId;

    // Constructors
    public ClientServiceDTO() {}

    public ClientServiceDTO(Long serviceId, String serviceName, String serviceDescription, 
                           BigDecimal servicePricePerSession, String serviceType, String modality,
                           Boolean transportIncluded, BigDecimal transportCost, String status,
                           String startDate, String endDate, BigDecimal totalPaid, Long contractId) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.servicePricePerSession = servicePricePerSession;
        this.serviceType = serviceType;
        this.modality = modality;
        this.transportIncluded = transportIncluded;
        this.transportCost = transportCost;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPaid = totalPaid;
        this.contractId = contractId;
    }

    // Getters and Setters
    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getServiceDescription() { return serviceDescription; }
    public void setServiceDescription(String serviceDescription) { this.serviceDescription = serviceDescription; }

    public BigDecimal getServicePricePerSession() { return servicePricePerSession; }
    public void setServicePricePerSession(BigDecimal servicePricePerSession) { this.servicePricePerSession = servicePricePerSession; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getModality() { return modality; }
    public void setModality(String modality) { this.modality = modality; }

    public Boolean getTransportIncluded() { return transportIncluded; }
    public void setTransportIncluded(Boolean transportIncluded) { this.transportIncluded = transportIncluded; }

    public BigDecimal getTransportCost() { return transportCost; }
    public void setTransportCost(BigDecimal transportCost) { this.transportCost = transportCost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }

    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
} 