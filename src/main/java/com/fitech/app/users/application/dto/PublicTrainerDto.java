package com.fitech.app.users.application.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for public trainer information (privacy-safe)")
public class PublicTrainerDto implements Serializable {
    @Schema(description = "Trainer ID", example = "123")
    private Integer id;
    
    @Schema(description = "Username", example = "trainer_carlos")
    private String username;
    
    @Schema(description = "User type", example = "2", allowableValues = {"1", "2"})
    private Integer type;
    
    @Schema(description = "Account creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
    
    @Schema(description = "Whether email is verified", example = "true")
    private Boolean isEmailVerified;
    
    @Schema(description = "Public person information")
    private PublicPersonDto person;
    
    @Schema(description = "Whether trainer has premium access", example = "false")
    private boolean isPremium = false;
    
    @Schema(description = "How premium access was obtained", example = "NONE")
    private PremiumBy premiumBy = PremiumBy.NONE;

    public PublicTrainerDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Boolean getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(Boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public PublicPersonDto getPerson() {
        return person;
    }

    public void setPerson(PublicPersonDto person) {
        this.person = person;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public PremiumBy getPremiumBy() {
        return premiumBy;
    }

    public void setPremiumBy(PremiumBy premiumBy) {
        this.premiumBy = premiumBy;
    }
} 