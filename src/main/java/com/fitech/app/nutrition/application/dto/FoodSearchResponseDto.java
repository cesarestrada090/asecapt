package com.fitech.app.nutrition.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodSearchResponseDto {
    private List<FoodItemDto> foods;
    private List<FoodCategoryDto> categories;
    private List<FoodItemDto> popularFoods;
    private Integer totalCount;
} 