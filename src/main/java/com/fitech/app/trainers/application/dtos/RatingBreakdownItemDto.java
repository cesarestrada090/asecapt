package com.fitech.app.trainers.application.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingBreakdownItemDto {
    private Integer stars;
    private Long count;
    private Double percentage;
} 