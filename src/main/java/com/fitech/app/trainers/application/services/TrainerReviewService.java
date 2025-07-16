package com.fitech.app.trainers.application.services;

import com.fitech.app.trainers.domain.entities.ServiceContract;
import com.fitech.app.trainers.domain.entities.TrainerReview;
import com.fitech.app.trainers.application.dto.CreateReviewDto;
import com.fitech.app.trainers.application.dto.ReviewResponseDto;
import com.fitech.app.trainers.application.dto.ReviewableContractDto;
import com.fitech.app.trainers.application.dtos.TrainerRatingBreakdownDto;
import com.fitech.app.trainers.application.dtos.RatingBreakdownItemDto;
import com.fitech.app.trainers.infrastructure.repositories.TrainerReviewRepository;
import com.fitech.app.trainers.infrastructure.repository.ServiceContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerReviewService {
    
    private final TrainerReviewRepository trainerReviewRepository;
    private final ServiceContractRepository serviceContractRepository;
    
    /**
     * Get all contracts that can be reviewed by a client
     */
    public List<ReviewableContractDto> getReviewableContracts(Integer clientId) {
        log.info("Getting reviewable contracts for client: {}", clientId);
        
        List<Object[]> rawResults = trainerReviewRepository.findReviewableContractsByClientIdRaw(clientId);
        
        return rawResults.stream().map(row -> {
            return ReviewableContractDto.builder()
                    .contractId((Integer) row[0])
                    .trainerId((Integer) row[1])
                    .trainerName((String) row[2])
                    .trainerProfilePhotoId((Integer) row[3])
                    .serviceId((Integer) row[4])
                    .serviceName((String) row[5])
                    .serviceDescription((String) row[6])
                    .contractStatus(row[7] != null ? row[7].toString() : "UNKNOWN") // Convert enum to string
                    .completionDate((java.time.LocalDateTime) row[8])
                    .endDate((java.time.LocalDateTime) row[9])
                    .hasReview((Boolean) row[10])
                    .existingReviewId((Integer) row[11])
                    .build();
        }).toList();
    }
    
    /**
     * Get all reviews created by a client
     */
    public List<ReviewResponseDto> getClientReviews(Integer clientId) {
        log.info("Getting reviews for client: {}", clientId);
        LocalDateTime editTimeLimit = LocalDateTime.now().minusHours(24);
        return trainerReviewRepository.findClientReviewsWithDetails(clientId, editTimeLimit);
    }
    
    /**
     * Create a new review for a trainer
     */
    @Transactional
    public ReviewResponseDto createReview(Integer clientId, CreateReviewDto createReviewDto) {
        log.info("Creating review for client: {} and contract: {}", 
                clientId, createReviewDto.getServiceContractId());
        
        // Validate contract exists and can be reviewed
        ServiceContract contract = serviceContractRepository.findById(createReviewDto.getServiceContractId())
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));
        
        // Validate client owns this contract
        if (!contract.getClientId().equals(clientId)) {
            throw new RuntimeException("No tienes autorización para calificar este contrato");
        }
        
        // Validate contract is completed or cancelled
        if (!isContractReviewable(contract)) {
            throw new RuntimeException("Solo puedes calificar contratos completados o cancelados");
        }
        
        // Check if review already exists
        if (trainerReviewRepository.existsByClientIdAndServiceContractId(clientId, contract.getId())) {
            throw new RuntimeException("Ya has calificado este contrato");
        }
        
        // Create review
        TrainerReview review = new TrainerReview();
        review.setTrainerId(contract.getTrainerId());
        review.setClientId(clientId);
        review.setServiceId(contract.getServiceId());
        review.setServiceContractId(contract.getId());
        review.setRating(createReviewDto.getRating());
        review.setComment(createReviewDto.getComment());
        review.setIsAnonymous(createReviewDto.getIsAnonymous() != null ? createReviewDto.getIsAnonymous() : false);
        
        TrainerReview savedReview = trainerReviewRepository.save(review);
        log.info("Review created successfully with ID: {}", savedReview.getId());
        
        // Return updated reviews list to refresh client view
        return getReviewById(savedReview.getId(), clientId);
    }
    
    /**
     * Update an existing review (only within 24 hours and if trainer hasn't responded)
     */
    @Transactional
    public ReviewResponseDto updateReview(Integer clientId, Integer reviewId, CreateReviewDto updateReviewDto) {
        log.info("Updating review: {} for client: {}", reviewId, clientId);
        
        TrainerReview review = trainerReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Calificación no encontrada"));
        
        // Validate client owns this review
        if (!review.getClientId().equals(clientId)) {
            throw new RuntimeException("No tienes autorización para editar esta calificación");
        }
        
        // Validate review can be edited
        if (!review.canBeEditedByClient()) {
            throw new RuntimeException("Ya no puedes editar esta calificación");
        }
        
        // Update review
        review.setRating(updateReviewDto.getRating());
        review.setComment(updateReviewDto.getComment());
        review.setIsAnonymous(updateReviewDto.getIsAnonymous() != null ? updateReviewDto.getIsAnonymous() : false);
        
        TrainerReview savedReview = trainerReviewRepository.save(review);
        log.info("Review updated successfully: {}", savedReview.getId());
        
        return getReviewById(savedReview.getId(), clientId);
    }
    
    /**
     * Delete a review (soft delete)
     */
    @Transactional
    public void deleteReview(Integer clientId, Integer reviewId) {
        log.info("Deleting review: {} for client: {}", reviewId, clientId);
        
        TrainerReview review = trainerReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Calificación no encontrada"));
        
        // Validate client owns this review
        if (!review.getClientId().equals(clientId)) {
            throw new RuntimeException("No tienes autorización para eliminar esta calificación");
        }
        
        // Validate review can be deleted (same rules as editing)
        if (!review.canBeEditedByClient()) {
            throw new RuntimeException("Ya no puedes eliminar esta calificación");
        }
        
        // Soft delete
        review.setIsActive(false);
        trainerReviewRepository.save(review);
        
        log.info("Review soft deleted successfully: {}", reviewId);
    }
    
    /**
     * Get all reviews for a trainer (public view)
     */
    public List<ReviewResponseDto> getTrainerReviews(Integer trainerId) {
        log.info("Getting all reviews for trainer: {}", trainerId);
        return trainerReviewRepository.findTrainerReviewsWithDetails(trainerId);
    }
    
    /**
     * Get trainer rating statistics
     */
    public TrainerRatingStatsDto getTrainerRatingStats(Integer trainerId) {
        log.info("Getting rating stats for trainer: {}", trainerId);
        
        Double averageRating = trainerReviewRepository.calculateTrainerAverageRating(trainerId);
        Long totalReviews = trainerReviewRepository.countTrainerReviews(trainerId);
        
        return TrainerRatingStatsDto.builder()
                .trainerId(trainerId)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .totalReviews(totalReviews != null ? totalReviews : 0)
                .build();
    }
    
        /**
     * Get trainer rating breakdown (stars distribution)
     */
    public TrainerRatingBreakdownDto getTrainerRatingBreakdown(Integer trainerId) {
        log.info("Getting rating breakdown for trainer: {}", trainerId);
        
        // Get total reviews count
        Long totalReviews = trainerReviewRepository.countTrainerReviews(trainerId);
        
        // Get average rating
        Double averageRating = trainerReviewRepository.calculateTrainerAverageRating(trainerId);
        
        // Get rating distribution
        List<RatingBreakdownItemDto> breakdown = new ArrayList<>();
        for (int i = 5; i >= 1; i--) { // 5 stars to 1 star
            Long count = trainerReviewRepository.countReviewsByRating(trainerId, i);
            long actualCount = count != null ? count : 0;
            double percentage = (totalReviews != null && totalReviews > 0) 
                ? (actualCount * 100.0) / totalReviews 
                : 0.0;
            
            breakdown.add(RatingBreakdownItemDto.builder()
                .stars(i)
                .count(actualCount)
                .percentage(Math.round(percentage * 10.0) / 10.0)
                .build());
        }
        
        return TrainerRatingBreakdownDto.builder()
            .totalReviews(totalReviews != null ? totalReviews : 0)
            .averageRating(averageRating != null ? averageRating : 0.0)
            .breakdown(breakdown)
            .build();
    }
    
    /**
     * Get trainer reviews with pagination
     */
    public Page<ReviewResponseDto> getTrainerReviewsPaginated(Integer trainerId, Pageable pageable) {
        log.info("Getting paginated reviews for trainer: {} with page: {}", trainerId, pageable.getPageNumber());
        return trainerReviewRepository.findTrainerReviewsWithDetailsPaginated(trainerId, pageable);
    }
    
    /**
     * Get trainer statistics (using existing method with different return type)
     */
    public TrainerRatingStatsDto getTrainerStats(Integer trainerId) {
        return getTrainerRatingStats(trainerId);
    }
    
    /**
     * Respond to a review
     */
    @Transactional
    public void respondToReview(Integer reviewId, Integer trainerId, String response) {
        log.info("Trainer {} responding to review: {}", trainerId, reviewId);
        
        TrainerReview review = trainerReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));
        
        // Validate trainer owns this review
        if (!review.getTrainerId().equals(trainerId)) {
            throw new IllegalArgumentException("No tienes autorización para responder a esta reseña");
        }
        
        // Check if already responded
        if (review.getTrainerResponse() != null) {
            throw new IllegalArgumentException("Ya has respondido a esta reseña");
        }
        
        // Validate response
        if (response == null || response.trim().isEmpty()) {
            throw new IllegalArgumentException("La respuesta es requerida");
        }
        
        if (response.length() > 500) {
            throw new IllegalArgumentException("La respuesta no puede exceder 500 caracteres");
        }
        
        // Update review with response
        review.setTrainerResponse(response.trim());
        review.setTrainerResponseDate(LocalDateTime.now());
        
        trainerReviewRepository.save(review);
        log.info("Response added to review: {}", reviewId);
    }
    
    // Helper methods
    private boolean isContractReviewable(ServiceContract contract) {
        return ServiceContract.ContractStatus.COMPLETED.equals(contract.getContractStatus()) || 
               ServiceContract.ContractStatus.CANCELLED.equals(contract.getContractStatus());
    }
    
    private ReviewResponseDto getReviewById(Integer reviewId, Integer clientId) {
        LocalDateTime editTimeLimit = LocalDateTime.now().minusHours(24);
        return trainerReviewRepository.findClientReviewsWithDetails(clientId, editTimeLimit)
                .stream()
                .filter(review -> review.getId().equals(reviewId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Calificación no encontrada"));
    }
    
    // Inner DTO class for trainer stats
    @lombok.Data
    @lombok.Builder
    public static class TrainerRatingStatsDto {
        private Integer trainerId;
        private Double averageRating;
        private Long totalReviews;
    }
} 