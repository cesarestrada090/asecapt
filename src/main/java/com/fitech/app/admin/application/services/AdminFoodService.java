package com.fitech.app.admin.application.services;

import com.fitech.app.admin.application.dto.AdminFoodStatsDto;
import com.fitech.app.admin.application.dto.CreateFoodCategoryRequestDto;
import com.fitech.app.admin.application.dto.CreateFoodItemRequestDto;
import com.fitech.app.admin.application.dto.UpdateFoodItemRequestDto;
import com.fitech.app.nutrition.application.dto.FoodCategoryDto;
import com.fitech.app.nutrition.application.dto.FoodItemDto;
import com.fitech.app.users.application.dto.ResultPage;
import org.springframework.data.domain.Pageable;

public interface AdminFoodService {
    
    // Food Items Management
    FoodItemDto createFoodItem(CreateFoodItemRequestDto requestDto, Integer adminUserId);
    FoodItemDto updateFoodItem(Integer id, UpdateFoodItemRequestDto requestDto, Integer adminUserId);
    void deleteFoodItem(Integer id);
    FoodItemDto getFoodItemById(Integer id);
    ResultPage<FoodItemDto> getAllFoodItems(Pageable pageable);
    ResultPage<FoodItemDto> searchFoodItems(String searchTerm, Pageable pageable);
    
    // Food Categories Management
    FoodCategoryDto createFoodCategory(CreateFoodCategoryRequestDto requestDto);
    FoodCategoryDto updateFoodCategory(Integer id, CreateFoodCategoryRequestDto requestDto);
    void deleteFoodCategory(Integer id);
    ResultPage<FoodCategoryDto> getAllFoodCategories(Pageable pageable);
    java.util.List<FoodCategoryDto> getAllFoodCategoriesNoPagination();
    
    // Statistics
    AdminFoodStatsDto getFoodStats();
} 