package com.fitech.app.trainers.application.dtos;

import com.fitech.app.trainers.domain.entities.TrainerRecognition;
import java.time.LocalDate;

public class TrainerRecognitionDto {
    
    private Long id;
    private String title;
    private String organization;
    private LocalDate date;
    private String type;
    private String level;
    private String description;
    private Boolean isRecent;
    
    // Constructors
    public TrainerRecognitionDto() {}
    
    public TrainerRecognitionDto(Long id, String title, String organization, 
                               LocalDate date, String type, String level, 
                               String description, Boolean isRecent) {
        this.id = id;
        this.title = title;
        this.organization = organization;
        this.date = date;
        this.type = type;
        this.level = level;
        this.description = description;
        this.isRecent = isRecent;
    }
    
    // Factory method to create from entity
    public static TrainerRecognitionDto fromEntity(TrainerRecognition recognition) {
        return new TrainerRecognitionDto(
            recognition.getId(),
            recognition.getTitle(),
            recognition.getOrganization(),
            recognition.getDate(),
            recognition.getType(),
            recognition.getLevel() != null ? recognition.getLevel() : null,
            recognition.getDescription(),
            recognition.isRecent()
        );
    }
    
    // Method to convert to entity
    public TrainerRecognition toEntity(Long trainerId) {
        TrainerRecognition recognition = new TrainerRecognition();
        recognition.setId(this.id);
        recognition.setTrainerId(trainerId);
        recognition.setTitle(this.title);
        recognition.setOrganization(this.organization);
        recognition.setDate(this.date);
        recognition.setType(this.type);
        if (this.level != null && !this.level.isEmpty()) {
            recognition.setLevel((this.level));
        }
        recognition.setDescription(this.description);
        return recognition;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public Boolean getIsRecent() {
        return isRecent;
    }
    
    public void setIsRecent(Boolean isRecent) {
        this.isRecent = isRecent;
    }
} 