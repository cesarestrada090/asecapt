package com.asecapt.app.users.domain.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;



@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 45)
    @Column(name = "username", length = 45)
    private String username;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Size(max = 256)
    @Column(name = "password", length = 256)
    private String password;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_email_verified", nullable = false)
    private Boolean isEmailVerified = false;

    @Column(name = "email_verification_token", length = 255)
    private String emailVerificationToken;

    @Column(name = "email_token_expires_at")
    private LocalDateTime emailTokenExpiresAt;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name =  "person_id")
    private Person person;
    


    @Column(name = "active", nullable = false)
    private boolean active = true;

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

    public String getPassword() {
        return password;
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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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



    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
}