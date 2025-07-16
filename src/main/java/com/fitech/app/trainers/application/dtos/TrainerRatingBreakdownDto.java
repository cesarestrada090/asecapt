package com.fitech.app.trainers.application.dtos;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TrainerRatingBreakdownDto {
    private Long totalReviews;
    private Double averageRating;
    private List<RatingBreakdownItemDto> breakdown;
} 