package com.asecapt.app.users.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "certificate_validation")
public class CertificateValidation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "certificate_id", nullable = false)
    private Integer certificateId;
    
    @Column(name = "validation_token", length = 100, nullable = false)
    private String validationToken;
    
    @Column(name = "validated_at", nullable = false)
    private LocalDateTime validatedAt;
    
    @Column(name = "validator_ip", length = 45)
    private String validatorIp;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "validation_result", length = 20, nullable = false)
    private String validationResult;
    
    @Column(name = "response_data", columnDefinition = "JSON")
    private String responseData;
    
    // Relación JPA
    // TODO: Add relationship when needed for complex queries
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "certificate_id", insertable = false, updatable = false)
    // private Certificate certificate;
    
    @PrePersist
    protected void onCreate() {
        if (validatedAt == null) {
            validatedAt = LocalDateTime.now();
        }
    }
} 