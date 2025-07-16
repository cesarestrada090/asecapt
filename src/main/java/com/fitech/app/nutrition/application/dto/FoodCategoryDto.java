package com.fitech.app.nutrition.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodCategoryDto {
    private Integer id;
    private String name;
    private String description;
    private String icon;
    private Boolean isActive;
} 