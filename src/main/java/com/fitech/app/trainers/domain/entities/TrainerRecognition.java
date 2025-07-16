package com.fitech.app.trainers.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "trainer_recognitions")
public class TrainerRecognition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "organization", nullable = false, length = 255)
    private String organization;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Column(name = "type", nullable = false)
    private String type;
    
    @Column(name = "level")
    private String level;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum RecognitionType {
        AWARD,
        ACHIEVEMENT,
        RECOGNITION,
        COMPETITION
    }
    
    public enum RecognitionLevel {
        LOCAL,
        REGIONAL,
        NACIONAL,
        INTERNACIONAL
    }
    
    // Constructors
    public TrainerRecognition() {
        this.createdAt = LocalDateTime.now();
    }
    
    public TrainerRecognition(Long trainerId, String title, String organization, 
                            LocalDate date, RecognitionType type, RecognitionLevel level, 
                            String description) {
        this();
        this.trainerId = trainerId;
        this.title = title;
        this.organization = organization;
        this.date = date;
        this.type = type.name();
        this.level = level.name();
        this.description = description;
    }
    
    // Update method
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean isRecent() {
        return date.isAfter(LocalDate.now().minusYears(2));
    }
    
    public String getDisplayLevel() {
        return level != null ? level : "Sin especificar";
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getOrganization() {
        return organization;
    }
    
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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