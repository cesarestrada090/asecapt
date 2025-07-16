package com.fitech.app.trainers.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "trainer_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerReview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "trainer_id", nullable = false)
    private Integer trainerId;
    
    @Column(name = "client_id", nullable = false)
    private Integer clientId;
    
    @Column(name = "service_id")
    private Integer serviceId;
    
    @Column(name = "service_contract_id")
    private Integer serviceContractId;
    
    @Column(name = "rating", nullable = false)
    private Integer rating; // 1-5 stars
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    
    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;
    
    @Column(name = "trainer_response", columnDefinition = "TEXT")
    private String trainerResponse;
    
    @Column(name = "trainer_response_date")
    private LocalDateTime trainerResponseDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public boolean hasTrainerResponse() {
        return trainerResponse != null && !trainerResponse.trim().isEmpty();
    }
    
    public boolean canBeEditedByClient() {
        // Client can edit within 24 hours if trainer hasn't responded
        return !hasTrainerResponse() && 
               createdAt.isAfter(LocalDateTime.now().minusHours(24));
    }
} 