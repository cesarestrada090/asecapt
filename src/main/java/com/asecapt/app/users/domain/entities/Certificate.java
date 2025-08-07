package com.asecapt.app.users.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
public class Certificate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "certificate_code", unique = true, nullable = false)
    private String certificateCode;
    
    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "qr_code_path")
    private String qrCodePath;
    
    @Column(name = "issued_date", nullable = false)
    private LocalDateTime issuedDate;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // Constructors
    public Certificate() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    public Certificate(String certificateCode, Enrollment enrollment, String filePath, 
                      String fileName, LocalDateTime issuedDate) {
        this();
        this.certificateCode = certificateCode;
        this.enrollment = enrollment;
        this.filePath = filePath;
        this.fileName = fileName;
        this.issuedDate = issuedDate;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getCertificateCode() {
        return certificateCode;
    }
    
    public void setCertificateCode(String certificateCode) {
        this.certificateCode = certificateCode;
    }
    
    public Enrollment getEnrollment() {
        return enrollment;
    }
    
    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getQrCodePath() {
        return qrCodePath;
    }
    
    public void setQrCodePath(String qrCodePath) {
        this.qrCodePath = qrCodePath;
    }
    
    public LocalDateTime getIssuedDate() {
        return issuedDate;
    }
    
    public void setIssuedDate(LocalDateTime issuedDate) {
        this.issuedDate = issuedDate;
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}