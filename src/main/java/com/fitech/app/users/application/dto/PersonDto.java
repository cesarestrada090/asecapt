package com.fitech.app.users.application.dto;

import com.fitech.app.users.domain.entities.User;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link User}
 */
@Schema(description = "DTO for person information and profile data")
public class PersonDto implements Serializable {
    @Schema(description = "Person ID", example = "123")
    private Integer id;
    @Size(max = 45)
    @Schema(description = "First name", example = "Juan", maxLength = 45)
    private String firstName;
    @Size(max = 45)
    @Schema(description = "Last name", example = "Pérez", maxLength = 45)
    private String lastName;
    @Schema(description = "Document number", example = "12345678")
    private String documentNumber;
    @Schema(description = "Phone number", example = "+51987654321")
    private String phoneNumber;
    @Schema(description = "Email address", example = "juan.perez@email.com")
    private String email;
    @Schema(description = "Document type", example = "DNI", allowableValues = {"DNI", "PASSPORT", "CE"})
    private String documentType = "DNI";
    @Schema(description = "Profile photo ID", example = "456")
    private Integer profilePhotoId;
    @Schema(description = "Presentation video ID", example = "789")
    private Integer presentationVideoId;
    @Schema(description = "Biography or description", example = "Entrenador personal certificado con 5 años de experiencia")
    private String bio;
    @Schema(description = "Gender", example = "M", allowableValues = {"M", "F", "OTHER"})
    private String gender;
    @Schema(description = "Birth date", example = "1990-05-15")
    private java.time.LocalDate birthDate;
    @Schema(description = "List of fitness goal types")
    private List<FitnessGoalTypeDto> fitnessGoalTypes;

    public PersonDto() {
    }

    public PersonDto(Integer id, String firstName, String lastName, String documentNumber, String phoneNumber, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentNumber = documentNumber;
        this.phoneNumber = phoneNumber;
        this.email = email;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public boolean hasDifferentDocumentNumber(String documentNumber){
        return this.getDocumentNumber()!= null && !this.getDocumentNumber().equals(documentNumber);
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public java.time.LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(java.time.LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public List<FitnessGoalTypeDto> getFitnessGoalTypes() {
        return fitnessGoalTypes;
    }

    public void setFitnessGoalTypes(List<FitnessGoalTypeDto> fitnessGoalTypes) {
        this.fitnessGoalTypes = fitnessGoalTypes;
    }
}