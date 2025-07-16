package com.fitech.app.trainers.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for trainer's client information and relationship details")
public class TrainerClientDTO {
    @Schema(description = "Client ID", example = "123")
    private Long clientId;
    
    @Schema(description = "Client full name", example = "Juan Pérez")
    private String clientName;
    
    @Schema(description = "Client email address", example = "juan.perez@email.com")
    private String clientEmail;
    
    @Schema(description = "Client phone number", example = "+51987654321")
    private String clientPhone;
    
    @Schema(description = "Client profile photo ID", example = "456")
    private Long profilePhotoId;
    
    @Schema(description = "Client fitness goals", example = "[\"Pérdida de peso\", \"Ganancia muscular\"]")
    private List<String> fitnessGoals;
    
    @Schema(description = "List of services the client has with this trainer")
    private List<ClientServiceDTO> services;
    
    @Schema(description = "Last contact date", example = "2024-01-15")
    private String lastContactDate;
    
    @Schema(description = "Next scheduled session date", example = "2024-01-20")
    private String nextSessionDate;
    
    @Schema(description = "Last trainer note about the client", example = "Cliente muy comprometido con sus objetivos")
    private String lastTrainerNote;
    
    @Schema(description = "Date of last trainer note", example = "2024-01-15")
    private String lastNoteDate;
    
    @Schema(description = "Total number of services contracted", example = "3")
    private Integer totalServicesCount;
    
    @Schema(description = "Number of active services", example = "1")
    private Integer activeServicesCount;
    
    @Schema(description = "Total amount paid by client", example = "750.00")
    private BigDecimal totalAmountPaid;

    // Constructors
    public TrainerClientDTO() {}

    public TrainerClientDTO(Long clientId, String clientName, String clientEmail, String clientPhone, 
                           Long profilePhotoId, List<String> fitnessGoals, List<ClientServiceDTO> services,
                           String lastContactDate, String nextSessionDate, String lastTrainerNote, 
                           String lastNoteDate, Integer totalServicesCount, Integer activeServicesCount,
                           BigDecimal totalAmountPaid) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.profilePhotoId = profilePhotoId;
        this.fitnessGoals = fitnessGoals;
        this.services = services;
        this.lastContactDate = lastContactDate;
        this.nextSessionDate = nextSessionDate;
        this.lastTrainerNote = lastTrainerNote;
        this.lastNoteDate = lastNoteDate;
        this.totalServicesCount = totalServicesCount;
        this.activeServicesCount = activeServicesCount;
        this.totalAmountPaid = totalAmountPaid;
    }

    // Getters and Setters
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }

    public Long getProfilePhotoId() { return profilePhotoId; }
    public void setProfilePhotoId(Long profilePhotoId) { this.profilePhotoId = profilePhotoId; }

    public List<String> getFitnessGoals() { return fitnessGoals; }
    public void setFitnessGoals(List<String> fitnessGoals) { this.fitnessGoals = fitnessGoals; }

    public List<ClientServiceDTO> getServices() { return services; }
    public void setServices(List<ClientServiceDTO> services) { this.services = services; }

    public String getLastContactDate() { return lastContactDate; }
    public void setLastContactDate(String lastContactDate) { this.lastContactDate = lastContactDate; }

    public String getNextSessionDate() { return nextSessionDate; }
    public void setNextSessionDate(String nextSessionDate) { this.nextSessionDate = nextSessionDate; }

    public String getLastTrainerNote() { return lastTrainerNote; }
    public void setLastTrainerNote(String lastTrainerNote) { this.lastTrainerNote = lastTrainerNote; }

    public String getLastNoteDate() { return lastNoteDate; }
    public void setLastNoteDate(String lastNoteDate) { this.lastNoteDate = lastNoteDate; }

    public Integer getTotalServicesCount() { return totalServicesCount; }
    public void setTotalServicesCount(Integer totalServicesCount) { this.totalServicesCount = totalServicesCount; }

    public Integer getActiveServicesCount() { return activeServicesCount; }
    public void setActiveServicesCount(Integer activeServicesCount) { this.activeServicesCount = activeServicesCount; }

    public BigDecimal getTotalAmountPaid() { return totalAmountPaid; }
    public void setTotalAmountPaid(BigDecimal totalAmountPaid) { this.totalAmountPaid = totalAmountPaid; }
} 