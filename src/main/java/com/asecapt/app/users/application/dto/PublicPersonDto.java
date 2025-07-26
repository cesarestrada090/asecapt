package com.asecapt.app.users.application.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for public person information (privacy-safe)")
public class PublicPersonDto implements Serializable {
    @Schema(description = "Person ID", example = "123")
    private Integer id;
    
    @Schema(description = "First name", example = "Juan")
    private String firstName;
    
    @Schema(description = "Last name", example = "Pérez")
    private String lastName;
    
    @Schema(description = "Document number", example = "12345678")
    private String documentNumber;
    
    @Schema(description = "Document type", example = "DNI", allowableValues = {"DNI", "PASSPORT", "CE"})
    private String documentType;
    // Note: phoneNumber and email are excluded for privacy
    
    @Schema(description = "Biography or description", example = "Entrenador personal certificado")
    private String bio;
    
    @Schema(description = "Profile photo ID", example = "456")
    private Integer profilePhotoId;
    
    @Schema(description = "Presentation video ID", example = "789")
    private Integer presentationVideoId;
    
    @Schema(description = "Gender", example = "M", allowableValues = {"M", "F", "OTHER"})
    private String gender;
    
    @Schema(description = "Birth date", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime birthDate;
    
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    public PublicPersonDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getProfilePhotoId() {
        return profilePhotoId;
    }

    public void setProfilePhotoId(Integer profilePhotoId) {
        this.profilePhotoId = profilePhotoId;
    }

    public Integer getPresentationVideoId() {
        return presentationVideoId;
    }

    public void setPresentationVideoId(Integer presentationVideoId) {
        this.presentationVideoId = presentationVideoId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 