package com.fitech.app.users.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO for updating user profile information")
public class ProfileUpdateDto {
    @NotBlank(message = "First name is required")
    @Size(max = 45, message = "First name must be less than 45 characters")
    @Schema(description = "User's first name", example = "Juan", maxLength = 45)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 45, message = "Last name must be less than 45 characters")
    @Schema(description = "User's last name", example = "Pérez", maxLength = 45)
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Schema(description = "User's phone number", example = "+51987654321")
    private String phoneNumber;

    // Email no se puede actualizar, solo se incluye para compatibilidad
    @Schema(description = "User's email (read-only)", example = "juan.perez@email.com", accessMode = Schema.AccessMode.READ_ONLY)
    private String email;

    @Size(max = 1200, message = "Bio must be less than 1200 characters")
    @Schema(description = "User's biography or description", example = "Entrenador personal certificado con 5 años de experiencia", maxLength = 1200)
    private String bio;

    @Schema(description = "List of fitness goal type IDs", example = "[1, 3, 5]")
    private List<Integer> fitnessGoalTypeIds;

    // Getters and Setters
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<Integer> getFitnessGoalTypeIds() {
        return fitnessGoalTypeIds;
    }

    public void setFitnessGoalTypeIds(List<Integer> fitnessGoalTypeIds) {
        this.fitnessGoalTypeIds = fitnessGoalTypeIds;
    }
} 