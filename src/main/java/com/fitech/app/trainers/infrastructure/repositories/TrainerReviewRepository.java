package com.fitech.app.trainers.infrastructure.repositories;

import com.fitech.app.trainers.domain.entities.TrainerReview;
import com.fitech.app.trainers.application.dto.ReviewResponseDto;
import com.fitech.app.trainers.application.dto.ReviewableContractDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerReviewRepository extends JpaRepository<TrainerReview, Integer> {
    
    // Check if client already reviewed a specific contract
    boolean existsByClientIdAndServiceContractId(Integer clientId, Integer serviceContractId);
    
    // Get review by client and contract
    Optional<TrainerReview> findByClientIdAndServiceContractId(Integer clientId, Integer serviceContractId);
    
    // Get all reviews by client
    List<TrainerReview> findByClientIdAndIsActiveTrueOrderByCreatedAtDesc(Integer clientId);
    
    // Get all reviews for a trainer
    List<TrainerReview> findByTrainerIdAndIsActiveTrueOrderByCreatedAtDesc(Integer trainerId);
    
    // Get contracts that can be reviewed by client - using Object[] for simplicity
    @Query("""
        SELECT 
            sc.id,
            sc.trainerId,
            COALESCE(CONCAT(tp.firstName, ' ', tp.lastName), 'Trainer sin nombre'),
            tp.profilePhotoId,
            sc.serviceId,
            COALESCE(ts.serviceType.name, 'Servicio'),
            COALESCE(ts.description, ''),
            sc.contractStatus,
            sc.completionDate,
            sc.endDate,
            CASE WHEN tr.id IS NOT NULL THEN true ELSE false END,
            tr.id
        FROM ServiceContract sc
        LEFT JOIN TrainerService ts ON ts.id = sc.serviceId
        LEFT JOIN User tu ON tu.id = sc.trainerId
        LEFT JOIN Person tp ON tp.id = tu.person.id
        LEFT JOIN TrainerReview tr ON tr.serviceContractId = sc.id AND tr.clientId = sc.clientId AND tr.isActive = true
        WHERE sc.clientId = :clientId 
        AND sc.contractStatus IN ('COMPLETED', 'CANCELLED')
        ORDER BY sc.completionDate DESC, sc.endDate DESC
    """)
    List<Object[]> findReviewableContractsByClientIdRaw(@Param("clientId") Integer clientId);
    
    // Get client reviews with detailed information
    @Query("""
        SELECT new com.fitech.app.trainers.application.dto.ReviewResponseDto(
            tr.id,
            tr.trainerId,
            CONCAT(tp.firstName, ' ', tp.lastName),
            tp.profilePhotoId,
            tr.clientId,
            CASE WHEN tr.isAnonymous = true THEN 'Usuario Anónimo' 
                 ELSE CONCAT(cp.firstName, ' ', cp.lastName) END,
            tr.serviceId,
            ts.serviceType.name,
            tr.serviceContractId,
            tr.rating,
            tr.comment,
            tr.isAnonymous,
            tr.trainerResponse,
            tr.trainerResponseDate,
            CASE WHEN tr.trainerResponse IS NULL AND tr.createdAt > :editTimeLimit THEN true ELSE false END,
            tr.createdAt,
            tr.updatedAt
        )
        FROM TrainerReview tr
        JOIN User tu ON tu.id = tr.trainerId
        JOIN Person tp ON tp.id = tu.person.id
        JOIN User cu ON cu.id = tr.clientId
        JOIN Person cp ON cp.id = cu.person.id
        LEFT JOIN TrainerService ts ON ts.id = tr.serviceId
        WHERE tr.clientId = :clientId 
        AND tr.isActive = true
        ORDER BY tr.createdAt DESC
    """)
    List<ReviewResponseDto> findClientReviewsWithDetails(@Param("clientId") Integer clientId, 
                                                        @Param("editTimeLimit") java.time.LocalDateTime editTimeLimit);
    
    // Calculate trainer rating statistics
    @Query("""
        SELECT AVG(tr.rating) 
        FROM TrainerReview tr 
        WHERE tr.trainerId = :trainerId AND tr.isActive = true
    """)
    Double calculateTrainerAverageRating(@Param("trainerId") Integer trainerId);
    
    @Query("""
        SELECT COUNT(tr) 
        FROM TrainerReview tr 
        WHERE tr.trainerId = :trainerId AND tr.isActive = true
    """)
    Long countTrainerReviews(@Param("trainerId") Integer trainerId);
    
    // Get trainer reviews with detailed information (public view)
    @Query("""
        SELECT new com.fitech.app.trainers.application.dto.ReviewResponseDto(
            tr.id,
            tr.trainerId,
            CONCAT(tp.firstName, ' ', tp.lastName),
            tp.profilePhotoId,
            tr.clientId,
            CASE WHEN tr.isAnonymous = true THEN 'Usuario Anónimo' 
                 ELSE CONCAT(cp.firstName, ' ', cp.lastName) END,
            tr.serviceId,
            ts.serviceType.name,
            tr.serviceContractId,
            tr.rating,
            tr.comment,
            tr.isAnonymous,
            tr.trainerResponse,
            tr.trainerResponseDate,
            false,
            tr.createdAt,
            tr.updatedAt
        )
        FROM TrainerReview tr
        JOIN User tu ON tu.id = tr.trainerId
        JOIN Person tp ON tp.id = tu.person.id
        JOIN User cu ON cu.id = tr.clientId
        JOIN Person cp ON cp.id = cu.person.id
        LEFT JOIN TrainerService ts ON ts.id = tr.serviceId
        WHERE tr.trainerId = :trainerId 
        AND tr.isActive = true
        ORDER BY tr.createdAt DESC
    """)
    List<ReviewResponseDto> findTrainerReviewsWithDetails(@Param("trainerId") Integer trainerId);
    
    // Count reviews by specific rating
    @Query("""
        SELECT COUNT(tr) 
        FROM TrainerReview tr 
        WHERE tr.trainerId = :trainerId AND tr.rating = :rating AND tr.isActive = true
    """)
    Long countReviewsByRating(@Param("trainerId") Integer trainerId, @Param("rating") Integer rating);

    // Get trainer reviews with detailed information (paginated)
    @Query("""
        SELECT new com.fitech.app.trainers.application.dto.ReviewResponseDto(
            tr.id,
            tr.trainerId,
            CONCAT(tp.firstName, ' ', tp.lastName),
            tp.profilePhotoId,
            tr.clientId,
            CASE WHEN tr.isAnonymous = true THEN 'Usuario Anónimo' 
                 ELSE CONCAT(cp.firstName, ' ', cp.lastName) END,
            tr.serviceId,
            ts.serviceType.name,
            tr.serviceContractId,
            tr.rating,
            tr.comment,
            tr.isAnonymous,
            tr.trainerResponse,
            tr.trainerResponseDate,
            false,
            tr.createdAt,
            tr.updatedAt
        )
        FROM TrainerReview tr
        JOIN User tu ON tu.id = tr.trainerId
        JOIN Person tp ON tp.id = tu.person.id
        JOIN User cu ON cu.id = tr.clientId
        JOIN Person cp ON cp.id = cu.person.id
        LEFT JOIN TrainerService ts ON ts.id = tr.serviceId
        WHERE tr.trainerId = :trainerId 
        AND tr.isActive = true
        ORDER BY tr.createdAt DESC
    """)
    Page<ReviewResponseDto> findTrainerReviewsWithDetailsPaginated(@Param("trainerId") Integer trainerId, Pageable pageable);
} 