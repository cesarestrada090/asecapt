package com.fitech.app.trainers.application.dtos;

import com.fitech.app.trainers.domain.entities.TrainerEducation;

public class TrainerEducationDto {
    
    private Long id;
    private String title;
    private String institution;
    private Integer year;
    private String type;
    private String description;
    
    // Constructors
    public TrainerEducationDto() {}
    
    public TrainerEducationDto(Long id, String title, String institution, 
                             Integer year, String type, String description) {
        this.id = id;
        this.title = title;
        this.institution = institution;
        this.year = year;
        this.type = type;
        this.description = description;
    }
    
    // Factory method to create from entity
    public static TrainerEducationDto fromEntity(TrainerEducation education) {
        return new TrainerEducationDto(
            education.getId(),
            education.getTitle(),
            education.getInstitution(),
            education.getYear(),
            education.getType(),
            education.getDescription()
        );
    }
    
    // Method to convert to entity
    public TrainerEducation toEntity(Long trainerId) {
        TrainerEducation education = new TrainerEducation();
        education.setId(this.id);
        education.setTrainerId(trainerId);
        education.setTitle(this.title);
        education.setInstitution(this.institution);
        education.setYear(this.year);
        education.setType(this.type);
        education.setDescription(this.description);
        return education;
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
} 