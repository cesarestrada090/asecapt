package com.fitech.app.trainers.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trainer_education")
public class TrainerEducation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "institution", nullable = false, length = 255)
    private String institution;
    
    @Column(name = "year", nullable = false)
    private Integer year;
    
    @Column(name = "type", nullable = false)
    private String type;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public TrainerEducation() {
        this.createdAt = LocalDateTime.now();
    }
    
    public TrainerEducation(Long trainerId, String title, String institution, 
                          Integer year, String type, String description) {
        this();
        this.trainerId = trainerId;
        this.title = title;
        this.institution = institution;
        this.year = year;
        this.type = type;
        this.description = description;
    }
    
    // Update method
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
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
    
    public String getInstitution() {
        return institution;
    }
    
    public void setInstitution(String institution) {
        this.institution = institution;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
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