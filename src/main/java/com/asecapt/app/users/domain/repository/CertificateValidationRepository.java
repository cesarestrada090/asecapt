package com.asecapt.app.users.domain.repository;

import com.asecapt.app.users.domain.entities.CertificateValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface CertificateValidationRepository extends JpaRepository<CertificateValidation, Integer> {
    
    // Find validations by certificate ID
    List<CertificateValidation> findByCertificateId(Integer certificateId);
    
    // Find validations by result
    List<CertificateValidation> findByValidationResult(String validationResult);
    
    // Find validations by IP address
    List<CertificateValidation> findByValidatorIp(String validatorIp);
    
    // Find validations in date range
    List<CertificateValidation> findByValidatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Count validations by certificate (Spring Data generates automatically)
    Long countByCertificateId(Integer certificateId);
    
    // Count validations by result (Spring Data generates automatically)
    Long countByValidationResult(String validationResult);
    
    // Get recent validations ordered by date (using Spring Data method naming)
    List<CertificateValidation> findAllByOrderByValidatedAtDesc();
} 