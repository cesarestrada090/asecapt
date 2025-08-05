package com.asecapt.app.users.domain.repository;

import com.asecapt.app.users.domain.entities.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {
    
    // Find certificate by verification token (for QR validation)
    Optional<Certificate> findByVerificationToken(String verificationToken);
    
    // Find certificate by certificate number
    Optional<Certificate> findByCertificateNumber(String certificateNumber);
    
    // Find certificate by enrollment ID
    Optional<Certificate> findByEnrollmentId(Integer enrollmentId);
    
    // Find certificates by status
    List<Certificate> findByStatus(String status);
    
    // Find certificates issued by user
    List<Certificate> findByIssuedByUserId(Integer issuedByUserId);
    
    // Find certificates with enrollment details for admin panel
    List<Certificate> findByStatusOrderByCreatedAtDesc(String status);
    
    // Get certificate stats (Spring Data generates this automatically)
    Long countByStatus(String status);
    
    // Get most scanned certificates (using Spring Data method naming)
    List<Certificate> findByStatusOrderByScanCountDesc(String status);
} 