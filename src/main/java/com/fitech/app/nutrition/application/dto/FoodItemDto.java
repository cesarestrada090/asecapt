package com.fitech.app.nutrition.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemDto {
    private Integer id;
    private String name;
    private String description;
    private String category;
    private String imageUrl;
    private Integer servingSize;  // gramos
    private MacroNutrientsDto macros;
    private Boolean popular;
    private Boolean isActive;
} 