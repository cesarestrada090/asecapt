package com.fitech.app.trainers.application.controllers;
import com.fitech.app.trainers.application.dto.CreateReviewDto;
import com.fitech.app.trainers.application.dto.ReviewResponseDto;
import com.fitech.app.trainers.application.dto.ReviewableContractDto;
import com.fitech.app.trainers.application.services.TrainerReviewService;
import com.fitech.app.users.domain.services.UserService;
import com.fitech.app.users.domain.entities.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/app/client/reviews")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Client Reviews", description = "Operations for managing client reviews and feedback")
@SecurityRequirement(name = "bearerAuth")
public class ClientReviewController {
    
    private final TrainerReviewService trainerReviewService;
    private final UserService userService;
    
    private Integer getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            User user = userService.getUserEntityByUsername(username);
            return user.getId();
        }
        return null;
    }
    
    /**
     * Get all contracts that can be reviewed by the current client
     */
    @GetMapping("/reviewable-contracts")
    public ResponseEntity<List<ReviewableContractDto>> getReviewableContracts() {
        try {
            Integer clientId = getCurrentUserId();
            if (clientId == null) {
                return ResponseEntity.badRequest().build();
            }
            
            log.info("Getting reviewable contracts for client: {}", clientId);
            
            List<ReviewableContractDto> contracts = trainerReviewService.getReviewableContracts(clientId);
            
            return ResponseEntity.ok(contracts);
                    
        } catch (Exception e) {
            log.error("Error getting reviewable contracts", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get all reviews created by the current client
     */
    @GetMapping("/my-reviews")
    public ResponseEntity<List<ReviewResponseDto>> getMyReviews() {
        try {
            Integer clientId = getCurrentUserId();
            if (clientId == null) {
                return ResponseEntity.badRequest().build();
            }
            
            log.info("Getting reviews for client: {}", clientId);
            
            List<ReviewResponseDto> reviews = trainerReviewService.getClientReviews(clientId);
            
            return ResponseEntity.ok(reviews);
                    
        } catch (Exception e) {
            log.error("Error getting client reviews", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Create a new review for a trainer
     */
    @PostMapping
    public ResponseEntity<?> createReview(
            @Valid @RequestBody CreateReviewDto createReviewDto) {
        try {
            Integer clientId = getCurrentUserId();
            if (clientId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Usuario no autenticado"));
            }
            
            log.info("Creating review for client: {} and contract: {}", 
                    clientId, createReviewDto.getServiceContractId());
            
            ReviewResponseDto review = trainerReviewService.createReview(clientId, createReviewDto);
            
            return ResponseEntity.ok(review);
                    
        } catch (Exception e) {
            log.error("Error creating review", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update an existing review
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Integer reviewId,
            @Valid @RequestBody CreateReviewDto updateReviewDto) {
        try {
            Integer clientId = getCurrentUserId();
            if (clientId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Usuario no autenticado"));
            }
            
            log.info("Updating review: {} for client: {}", reviewId, clientId);
            
            ReviewResponseDto review = trainerReviewService.updateReview(clientId, reviewId, updateReviewDto);
            
            return ResponseEntity.ok(review);
                    
        } catch (Exception e) {
            log.error("Error updating review", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete a review (soft delete)
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Integer reviewId) {
        try {
            Integer clientId = getCurrentUserId();
            if (clientId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Usuario no autenticado"));
            }
            
            log.info("Deleting review: {} for client: {}", reviewId, clientId);
            
            trainerReviewService.deleteReview(clientId, reviewId);
            
            return ResponseEntity.ok(Map.of("message", "Calificación eliminada exitosamente"));
                    
        } catch (Exception e) {
            log.error("Error deleting review", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get trainer rating statistics (public endpoint)
     */
    @GetMapping("/trainer/{trainerId}/stats")
    public ResponseEntity<TrainerReviewService.TrainerRatingStatsDto> getTrainerStats(
            @PathVariable Integer trainerId) {
        try {
            log.info("Getting rating stats for trainer: {}", trainerId);
            
            TrainerReviewService.TrainerRatingStatsDto stats = 
                    trainerReviewService.getTrainerRatingStats(trainerId);
            
            return ResponseEntity.ok(stats);
                    
        } catch (Exception e) {
            log.error("Error getting trainer stats", e);
            return ResponseEntity.badRequest().build();
        }
    }
} 