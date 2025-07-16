package com.fitech.app.users.application.dto;

import com.fitech.app.users.domain.entities.User;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO for {@link User}
 */
@Schema(description = "DTO for complete user information including sensitive data")
public class UserDto implements Serializable {
    @Schema(description = "User ID", example = "123")
    private Integer id;
    @Size(max = 45)
    @Schema(description = "Username", example = "usuario123", maxLength = 45)
    private String username;
    @Schema(description = "User type", example = "1", allowableValues = {"1", "2"})
    private Integer type;
    @Size(max = 45)
    @Schema(description = "Password", example = "password123", maxLength = 45, accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;
    @Schema(description = "Account creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
    @Schema(description = "Whether email is verified", example = "true")
    private Boolean isEmailVerified;
    @Schema(description = "Email verification token", accessMode = Schema.AccessMode.READ_ONLY)
    private String emailVerificationToken;
    @Schema(description = "Email token expiration timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime emailTokenExpiresAt;
    @Schema(description = "Person information associated with the user")
    private PersonDto person;
    @Schema(description = "Whether user has premium access", example = "false")
    private boolean isPremium = false;
    @Schema(description = "How premium access was obtained", example = "NONE")
    private PremiumBy premiumBy = PremiumBy.NONE;
    @Schema(description = "Whether user is active", example = "true")
    private boolean active = true;

    public UserDto() {
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isEmailVerified=" + isEmailVerified +
                ", emailVerificationToken='" + emailVerificationToken + '\'' +
                ", emailTokenExpiresAt=" + emailTokenExpiresAt +
                ", person=" + person +
                '}';
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    public LocalDateTime getEmailTokenExpiresAt() {
        return emailTokenExpiresAt;
    }

    public void setEmailTokenExpiresAt(LocalDateTime emailTokenExpiresAt) {
        this.emailTokenExpiresAt = emailTokenExpiresAt;
    }

    public boolean hasDifferentUserName(String username){
        return this.getUsername()!= null && !this.getUsername().equals(username);
    }

    public PersonDto getPerson() {
        return person;
    }

    public void setPerson(PersonDto person) {
        this.person = person;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
}