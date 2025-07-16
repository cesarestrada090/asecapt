package com.fitech.app.trainers.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "trainer_certifications")
public class TrainerCertification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "organization", nullable = false, length = 255)
    private String organization;
    
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;
    
    @Column(name = "expiration_date")
    private LocalDate expirationDate;
    
    @Column(name = "credential_id", length = 100)
    private String credentialId;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public TrainerCertification() {
        this.createdAt = LocalDateTime.now();
        this.isVerified = false;
    }
    
    public TrainerCertification(Long trainerId, String name, String organization, 
                              LocalDate issueDate, LocalDate expirationDate, 
                              String credentialId, String description) {
        this();
        this.trainerId = trainerId;
        this.name = name;
        this.organization = organization;
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
        this.credentialId = credentialId;
        this.description = description;
    }
    
    // Update method
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDate.now());
    }
    
    public boolean isValid() {
        return !isExpired();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTrainerId() {
        return trainerId;
    }
    
    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 