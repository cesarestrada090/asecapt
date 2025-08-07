package com.asecapt.app.users.infrastructure.repository;

import com.asecapt.app.users.domain.entities.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Integer> {
    
    /**
     * Find certificate by certificate code
     */
    Optional<Certificate> findByCertificateCode(String certificateCode);
    
    /**
     * Find certificate by enrollment ID
     */
    Optional<Certificate> findByEnrollmentId(Integer enrollmentId);
    
    /**
     * Find all certificates for a specific student
     */
    @Query("SELECT c FROM Certificate c " +
           "JOIN c.enrollment e " +
           "JOIN e.user u " +
           "WHERE u.id = :studentId AND c.isActive = true " +
           "ORDER BY c.issuedDate DESC")
    List<Certificate> findByStudentId(@Param("studentId") Integer studentId);
    
    /**
     * Find all certificates for a specific program
     */
    @Query("SELECT c FROM Certificate c " +
           "JOIN c.enrollment e " +
           "JOIN e.program p " +
           "WHERE p.id = :programId AND c.isActive = true " +
           "ORDER BY c.issuedDate DESC")
    List<Certificate> findByProgramId(@Param("programId") Integer programId);
    
    /**
     * Check if certificate exists for enrollment
     */
    boolean existsByEnrollmentIdAndIsActiveTrue(Integer enrollmentId);
    
    /**
     * Find all active certificates
     */
    List<Certificate> findByIsActiveTrueOrderByIssuedDateDesc();
    
    /**
     * Find certificates by student document number
     */
    @Query("SELECT c FROM Certificate c " +
           "JOIN c.enrollment e " +
           "JOIN e.user u " +
           "JOIN u.person p " +
           "WHERE p.documentNumber = :documentNumber AND c.isActive = true " +
           "ORDER BY c.issuedDate DESC")
    List<Certificate> findByStudentDocumentNumber(@Param("documentNumber") String documentNumber);
}