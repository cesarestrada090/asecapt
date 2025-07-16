package com.fitech.app.trainers.application.controllers;

import com.fitech.app.trainers.application.dto.ReviewResponseDto;
import com.fitech.app.trainers.application.services.TrainerReviewService;
import com.fitech.app.trainers.application.dtos.TrainerRatingBreakdownDto;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/app/trainer/reviews")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Trainer Reviews", description = "Operations for managing trainer reviews and ratings")
@SecurityRequirement(name = "bearerAuth")
public class TrainerReviewController {
    
    private final TrainerReviewService trainerReviewService;
    
    /**
     * Get all reviews for a specific trainer (public endpoint)
     */
    @GetMapping("/{trainerId}")
    public ResponseEntity<List<ReviewResponseDto>> getTrainerReviews(
            @PathVariable Integer trainerId) {
        try {
            log.info("Getting reviews for trainer: {}", trainerId);
            
            List<ReviewResponseDto> reviews = trainerReviewService.getTrainerReviews(trainerId);
            
            return ResponseEntity.ok(reviews);
                    
        } catch (Exception e) {
            log.error("Error getting trainer reviews", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get trainer rating statistics (public endpoint)
     */
    @GetMapping("/{trainerId}/stats")
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
    
    /**
     * Get trainer rating breakdown (public endpoint)
     */
    @GetMapping("/{trainerId}/breakdown")
    public ResponseEntity<TrainerRatingBreakdownDto> getTrainerRatingBreakdown(
            @PathVariable Integer trainerId) {
        try {
            log.info("Getting rating breakdown for trainer: {}", trainerId);
            
            TrainerRatingBreakdownDto breakdown = trainerReviewService.getTrainerRatingBreakdown(trainerId);
            
            return ResponseEntity.ok(breakdown);
                    
        } catch (Exception e) {
            log.error("Error getting trainer rating breakdown", e);
            return ResponseEntity.badRequest().build();
        }
    }
} 