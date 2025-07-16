package com.fitech.app.users.application.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for user response information without sensitive data")
public class UserResponseDto implements Serializable {
    @Schema(description = "User ID", example = "123")
    private Integer id;
    
    @Schema(description = "Username", example = "usuario123")
    private String username;
    
    @Schema(description = "User type", example = "1", allowableValues = {"1", "2"})
    private Integer type;
    
    @Schema(description = "Account creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
    
    @Schema(description = "Whether email is verified", example = "true")
    private Boolean isEmailVerified;
    
    @Schema(description = "Person information associated with the user")
    private PersonDto person;
    
    @Schema(description = "Whether user has premium access", example = "false")
    private boolean isPremium = false;
    
    @Schema(description = "How premium access was obtained", example = "NONE")
    private PremiumBy premiumBy = PremiumBy.NONE;

    @Schema(description = "Whether user is active", example = "true")
    private boolean active = true;

    public UserResponseDto() {
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

    public PersonDto getPerson() {
        return person;
    }

    public void setPerson(PersonDto person) {
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserResponseDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", type=" + type +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isEmailVerified=" + isEmailVerified +
                ", person=" + person +
                ", isPremium=" + isPremium +
                ", premiumBy=" + premiumBy +
                ", active=" + active +
                '}';
    }
} 