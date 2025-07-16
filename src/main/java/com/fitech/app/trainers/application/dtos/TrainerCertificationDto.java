package com.fitech.app.trainers.application.dtos;

import com.fitech.app.trainers.domain.entities.TrainerCertification;
import java.time.LocalDate;

public class TrainerCertificationDto {
    
    private Long id;
    private String name;
    private String organization;
    private LocalDate issueDate;
    private LocalDate expirationDate;
    private String credentialId;
    private String description;
    private Boolean isVerified;
    private Boolean isExpired;
    
    // Constructors
    public TrainerCertificationDto() {}
    
    public TrainerCertificationDto(Long id, String name, String organization, 
                                 LocalDate issueDate, LocalDate expirationDate, 
                                 String credentialId, String description, 
                                 Boolean isVerified, Boolean isExpired) {
        this.id = id;
        this.name = name;
        this.organization = organization;
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
        this.credentialId = credentialId;
        this.description = description;
        this.isVerified = isVerified;
        this.isExpired = isExpired;
    }
    
    // Factory method to create from entity
    public static TrainerCertificationDto fromEntity(TrainerCertification certification) {
        return new TrainerCertificationDto(
            certification.getId(),
            certification.getName(),
            certification.getOrganization(),
            certification.getIssueDate(),
            certification.getExpirationDate(),
            certification.getCredentialId(),
            certification.getDescription(),
            certification.getIsVerified(),
            certification.isExpired()
        );
    }
    
    // Method to convert to entity
    public TrainerCertification toEntity(Long trainerId) {
        TrainerCertification certification = new TrainerCertification();
        certification.setId(this.id);
        certification.setTrainerId(trainerId);
        certification.setName(this.name);
        certification.setOrganization(this.organization);
        certification.setIssueDate(this.issueDate);
        certification.setExpirationDate(this.expirationDate);
        certification.setCredentialId(this.credentialId);
        certification.setDescription(this.description);
        certification.setIsVerified(this.isVerified != null ? this.isVerified : false);
        return certification;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getOrganization() {
        return organization;
    }
    
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    
    public LocalDate getIssueDate() {
        return issueDate;
    }
    
    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
    
    public LocalDate getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    public String getCredentialId() {
        return credentialId;
    }
    
    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public Boolean getIsExpired() {
        return isExpired;
    }
    
    public void setIsExpired(Boolean isExpired) {
        this.isExpired = isExpired;
    }
} 