package com.fitech.app.admin.application.dto;

import lombok.Data;

@Data
public class AdminFoodStatsDto {
    
    private Long totalFoodItems;
    private Long activeFoodItems;
    private Long inactiveFoodItems;
    private Long popularFoodItems;
    private Long totalCategories;
    private Long activeCategories;
    private String lastUpdated;
    
    public AdminFoodStatsDto() {}
    
    public AdminFoodStatsDto(Long totalFoodItems, Long activeFoodItems, Long inactiveFoodItems, 
                            Long popularFoodItems, Long totalCategories, Long activeCategories) {
        this.totalFoodItems = totalFoodItems;
        this.activeFoodItems = activeFoodItems;
        this.inactiveFoodItems = inactiveFoodItems;
        this.popularFoodItems = popularFoodItems;
        this.totalCategories = totalCategories;
        this.activeCategories = activeCategories;
        this.lastUpdated = java.time.LocalDateTime.now().toString();
    }
} 