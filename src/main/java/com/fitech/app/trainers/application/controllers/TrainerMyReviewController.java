package com.fitech.app.trainers.application.controllers;

import com.fitech.app.trainers.application.services.TrainerReviewService;
import com.fitech.app.trainers.application.dto.ReviewResponseDto;
import com.fitech.app.trainers.application.services.TrainerReviewService.TrainerRatingStatsDto;
import com.fitech.app.trainers.application.dtos.TrainerRatingBreakdownDto;
import com.fitech.app.users.domain.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/app/trainer/my-reviews")
@RequiredArgsConstructor
@Tag(name = "Trainer My Reviews", description = "Operations for trainers to manage their own reviews")
@SecurityRequirement(name = "bearerAuth")
public class TrainerMyReviewController {

    private final TrainerReviewService trainerReviewService;
    private final UserService userService;

    private Integer getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserEntityByUsername(username).getId();
    }

    @GetMapping("/stats")
    public ResponseEntity<TrainerRatingStatsDto> getMyStats() {
        try {
            Integer trainerId = getCurrentUserId();
            TrainerRatingStatsDto stats = trainerReviewService.getTrainerStats(trainerId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/breakdown")
    public ResponseEntity<TrainerRatingBreakdownDto> getMyRatingBreakdown() {
        try {
            Integer trainerId = getCurrentUserId();
            TrainerRatingBreakdownDto breakdown = trainerReviewService.getTrainerRatingBreakdown(trainerId);
            return ResponseEntity.ok(breakdown);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponseDto>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Integer trainerId = getCurrentUserId();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<ReviewResponseDto> reviews = trainerReviewService.getTrainerReviewsPaginated(trainerId, pageable);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{reviewId}/respond")
    public ResponseEntity<?> respondToReview(
            @PathVariable Integer reviewId,
            @RequestBody Map<String, String> request) {
        try {
            Integer trainerId = getCurrentUserId();
            String response = request.get("response");
            
            if (response == null || response.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "La respuesta es requerida"));
            }

            if (response.length() > 500) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "La respuesta no puede exceder 500 caracteres"));
            }

            trainerReviewService.respondToReview(reviewId, trainerId, response.trim());
            return ResponseEntity.ok(Map.of("message", "Respuesta enviada exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error interno del servidor"));
        }
    }
} 