package com.fitech.app.trainers.infrastructure.repositories;

import com.fitech.app.trainers.domain.entities.TrainerCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerCertificationRepository extends JpaRepository<TrainerCertification, Long> {
    
    /**
     * Find all certifications for a specific trainer
     */
    @Query("SELECT tc FROM TrainerCertification tc WHERE tc.trainerId = :trainerId ORDER BY tc.issueDate DESC")
    List<TrainerCertification> findByTrainerIdOrderByIssueDateDesc(@Param("trainerId") Long trainerId);
    
    /**
     * Find certification by trainer and ID (for security validation)
     */
    @Query("SELECT tc FROM TrainerCertification tc WHERE tc.id = :id AND tc.trainerId = :trainerId")
    Optional<TrainerCertification> findByIdAndTrainerId(@Param("id") Long id, @Param("trainerId") Long trainerId);
    
    /**
     * Find valid (non-expired) certifications for a trainer
     */
    @Query("SELECT tc FROM TrainerCertification tc WHERE tc.trainerId = :trainerId " +
           "AND (tc.expirationDate IS NULL OR tc.expirationDate >= :currentDate) " +
           "ORDER BY tc.issueDate DESC")
    List<TrainerCertification> findValidCertificationsByTrainerId(@Param("trainerId") Long trainerId, 
                                                                @Param("currentDate") LocalDate currentDate);
    
    /**
     * Find expired certifications for a trainer
     */
    @Query("SELECT tc FROM TrainerCertification tc WHERE tc.trainerId = :trainerId " +
           "AND tc.expirationDate IS NOT NULL AND tc.expirationDate < :currentDate " +
           "ORDER BY tc.expirationDate DESC")
    List<TrainerCertification> findExpiredCertificationsByTrainerId(@Param("trainerId") Long trainerId, 
                                                                  @Param("currentDate") LocalDate currentDate);
    
    /**
     * Find verified certifications for a trainer
     */
    @Query("SELECT tc FROM TrainerCertification tc WHERE tc.trainerId = :trainerId AND tc.isVerified = true " +
           "ORDER BY tc.issueDate DESC")
    List<TrainerCertification> findVerifiedCertificationsByTrainerId(@Param("trainerId") Long trainerId);
    
    /**
     * Find certifications expiring soon (within next 30 days)
     */
    @Query("SELECT tc FROM TrainerCertification tc WHERE tc.trainerId = :trainerId " +
           "AND tc.expirationDate IS NOT NULL " +
           "AND tc.expirationDate BETWEEN :currentDate AND :futureDate " +
           "ORDER BY tc.expirationDate ASC")
    List<TrainerCertification> findCertificationsExpiringSoon(@Param("trainerId") Long trainerId, 
                                                            @Param("currentDate") LocalDate currentDate,
                                                            @Param("futureDate") LocalDate futureDate);
    
    /**
     * Count certifications for a trainer
     */
    @Query("SELECT COUNT(tc) FROM TrainerCertification tc WHERE tc.trainerId = :trainerId")
    Long countByTrainerId(@Param("trainerId") Long trainerId);
    
    /**
     * Count valid certifications for a trainer
     */
    @Query("SELECT COUNT(tc) FROM TrainerCertification tc WHERE tc.trainerId = :trainerId " +
           "AND (tc.expirationDate IS NULL OR tc.expirationDate >= :currentDate)")
    Long countValidCertificationsByTrainerId(@Param("trainerId") Long trainerId, 
                                           @Param("currentDate") LocalDate currentDate);
    
    /**
     * Check if trainer has certifications
     */
    @Query("SELECT CASE WHEN COUNT(tc) > 0 THEN true ELSE false END FROM TrainerCertification tc WHERE tc.trainerId = :trainerId")
    boolean existsByTrainerId(@Param("trainerId") Long trainerId);
    
    /**
     * Find certification by credential ID (for verification)
     */
    @Query("SELECT tc FROM TrainerCertification tc WHERE tc.credentialId = :credentialId")
    Optional<TrainerCertification> findByCredentialId(@Param("credentialId") String credentialId);
    
    /**
     * Delete all certifications for a trainer (for cleanup operations)
     */
    void deleteByTrainerId(@Param("trainerId") Long trainerId);
} 