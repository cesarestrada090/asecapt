package com.asecapt.app.users.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "certificate")
public class Certificate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "enrollment_id", nullable = false)
    private Integer enrollmentId;
    
    @Column(name = "certificate_number", length = 50, nullable = false, unique = true)
    private String certificateNumber;
    
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;
    
    @Column(name = "expiration_date")
    private LocalDate expirationDate;
    
    @Column(name = "certificate_file_path", length = 500)
    private String certificateFilePath;
    
    @Column(name = "verification_token", length = 100, nullable = false, unique = true)
    private String verificationToken;
    
    @Column(name = "verification_url", length = 500, nullable = false)
    private String verificationUrl;
    
    @Column(name = "status", length = 20, nullable = false)
    private String status = "active";
    
    @Column(name = "issued_by_user_id")
    private Integer issuedByUserId;
    
    @Column(name = "scan_count", nullable = false)
    private Integer scanCount = 0;
    
    @Column(name = "last_scanned_at")
    private LocalDateTime lastScannedAt;
    
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
    
    @Column(name = "revoked_reason", columnDefinition = "TEXT")
    private String revokedReason;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relaciones JPA
    // TODO: Add relationships when needed for complex queries
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "enrollment_id", insertable = false, updatable = false)
    // private Enrollment enrollment;
    
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "issued_by_user_id", insertable = false, updatable = false)
    // private User issuedByUser;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (issueDate == null) {
            issueDate = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 