package com.asecapt.app.users.application.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for client profile information with service history")
public class ClientProfileDTO {
    @Schema(description = "Client ID", example = "123")
    private Long id;
    
    @Schema(description = "Client first name", example = "Juan")
    private String firstName;
    
    @Schema(description = "Client last name", example = "Pérez")
    private String lastName;
    
    @Schema(description = "Document number", example = "12345678")
    private String documentNumber;
    
    @Schema(description = "Document type", example = "DNI", allowableValues = {"DNI", "PASSPORT", "CE"})
    private String documentType;
    
    @Schema(description = "Client biography", example = "Persona comprometida con el fitness y vida saludable")
    private String bio;
    
    @Schema(description = "Profile photo ID", example = "456")
    private Integer profilePhotoId;
    
    @Schema(description = "Gender", example = "M", allowableValues = {"M", "F", "OTHER"})
    private String gender;
    
    @Schema(description = "Birth date", example = "1990-05-15")
    private String birthDate;
    
    @Schema(description = "List of fitness goals", example = "[\"Pérdida de peso\", \"Ganancia muscular\"]")
    private List<String> fitnessGoals;
    
    @Schema(description = "List of contracted services")
    private List<ClientServiceInfo> services;

    // Clase interna para información de servicios
    @Schema(description = "Information about a service contracted by the client")
    public static class ClientServiceInfo {
        @Schema(description = "Service name", example = "Entrenamiento Personal")
        private String serviceName;
        
        @Schema(description = "Trainer name", example = "Carlos Trainer")
        private String trainerName;
        
        @Schema(description = "Contract status", example = "ACTIVE", allowableValues = {"PENDING", "ACTIVE", "COMPLETED", "CANCELLED"})
        private String contractStatus;
        
        @Schema(description = "Service modality", example = "PRESENCIAL", allowableValues = {"PRESENCIAL", "VIRTUAL", "HIBRIDO"})
        private String modality;
        
        @Schema(description = "Service start date", example = "2024-01-15")
        private String startDate;
        
        @Schema(description = "Total amount paid", example = "300.0")
        private Double totalAmount;

        // Constructors
        public ClientServiceInfo() {}

        public ClientServiceInfo(String serviceName, String trainerName, String contractStatus, 
                               String modality, String startDate, Double totalAmount) {
            this.serviceName = serviceName;
            this.trainerName = trainerName;
            this.contractStatus = contractStatus;
            this.modality = modality;
            this.startDate = startDate;
            this.totalAmount = totalAmount;
        }

        // Getters and Setters
        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }

        public String getTrainerName() { return trainerName; }
        public void setTrainerName(String trainerName) { this.trainerName = trainerName; }

        public String getContractStatus() { return contractStatus; }
        public void setContractStatus(String contractStatus) { this.contractStatus = contractStatus; }

        public String getModality() { return modality; }
        public void setModality(String modality) { this.modality = modality; }

        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }

        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    }

    // Constructors
    public ClientProfileDTO() {}

    public ClientProfileDTO(Long id, String firstName, String lastName, String documentNumber, 
                           String documentType, String bio, Integer profilePhotoId, String gender,
                           String birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentNumber = documentNumber;
        this.documentType = documentType;
        this.bio = bio;
        this.profilePhotoId = profilePhotoId;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Integer getProfilePhotoId() { return profilePhotoId; }
    public void setProfilePhotoId(Integer profilePhotoId) { this.profilePhotoId = profilePhotoId; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public List<String> getFitnessGoals() { return fitnessGoals; }
    public void setFitnessGoals(List<String> fitnessGoals) { this.fitnessGoals = fitnessGoals; }

    public List<ClientServiceInfo> getServices() { return services; }
    public void setServices(List<ClientServiceInfo> services) { this.services = services; }
} 