package com.fitech.app.admin.application.services.impl;

import com.fitech.app.admin.application.dto.AdminFoodStatsDto;
import com.fitech.app.admin.application.dto.CreateFoodCategoryRequestDto;
import com.fitech.app.admin.application.dto.CreateFoodItemRequestDto;
import com.fitech.app.admin.application.dto.UpdateFoodItemRequestDto;
import com.fitech.app.admin.application.services.AdminFoodService;
import com.fitech.app.commons.util.MapperUtil;
import com.fitech.app.commons.util.PaginationUtil;
import com.fitech.app.nutrition.application.dto.FoodCategoryDto;
import com.fitech.app.nutrition.application.dto.FoodItemDto;
import com.fitech.app.nutrition.application.dto.MacroNutrientsDto;
import com.fitech.app.nutrition.domain.entities.FoodCategory;
import com.fitech.app.nutrition.domain.entities.FoodItem;
import com.fitech.app.nutrition.infrastructure.repositories.FoodCategoryRepository;
import com.fitech.app.nutrition.infrastructure.repositories.FoodItemRepository;
import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.domain.entities.User;
import com.fitech.app.users.infrastructure.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AdminFoodServiceImpl implements AdminFoodService {
    
    private final FoodItemRepository foodItemRepository;
    private final FoodCategoryRepository foodCategoryRepository;
    private final UserRepository userRepository;
    
    public AdminFoodServiceImpl(FoodItemRepository foodItemRepository, 
                               FoodCategoryRepository foodCategoryRepository,
                               UserRepository userRepository) {
        this.foodItemRepository = foodItemRepository;
        this.foodCategoryRepository = foodCategoryRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public FoodItemDto createFoodItem(CreateFoodItemRequestDto requestDto, Integer adminUserId) {
        // Validate category exists
        FoodCategory category = foodCategoryRepository.findById(requestDto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + requestDto.getCategoryId()));
        
        // Get the admin user
        User adminUser = userRepository.findById(adminUserId)
            .orElseThrow(() -> new RuntimeException("Usuario administrador no encontrado con ID: " + adminUserId));
        
        FoodItem foodItem = new FoodItem();
        foodItem.setName(requestDto.getName());
        foodItem.setDescription(requestDto.getDescription());
        foodItem.setCategory(category);
        foodItem.setImageUrl(requestDto.getImageUrl());
        foodItem.setServingSizeGrams(requestDto.getServingSizeGrams());
        foodItem.setCaloriesPerServing(requestDto.getCaloriesPerServing());
        foodItem.setProteinsPerServing(requestDto.getProteinsPerServing());
        foodItem.setCarbohydratesPerServing(requestDto.getCarbohydratesPerServing());
        foodItem.setFatsPerServing(requestDto.getFatsPerServing());
        foodItem.setFiberPerServing(requestDto.getFiberPerServing());
        foodItem.setSugarPerServing(requestDto.getSugarPerServing());
        foodItem.setSodiumPerServing(requestDto.getSodiumPerServing());
        foodItem.setIsPopular(requestDto.getIsPopular());
        foodItem.setIsActive(requestDto.getIsActive());
        foodItem.setCreatedBy(adminUser);
        foodItem.setCreatedAt(LocalDateTime.now());
        foodItem.setUpdatedAt(LocalDateTime.now());
        
        FoodItem savedItem = foodItemRepository.save(foodItem);
        return convertToDto(savedItem);
    }
    
    @Override
    public FoodItemDto updateFoodItem(Integer id, UpdateFoodItemRequestDto requestDto, Integer adminUserId) {
        FoodItem existingItem = foodItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alimento no encontrado con ID: " + id));
        
        // Update only non-null fields
        if (requestDto.getName() != null) {
            existingItem.setName(requestDto.getName());
        }
        if (requestDto.getDescription() != null) {
            existingItem.setDescription(requestDto.getDescription());
        }
        if (requestDto.getCategoryId() != null) {
            FoodCategory category = foodCategoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + requestDto.getCategoryId()));
            existingItem.setCategory(category);
        }
        if (requestDto.getImageUrl() != null) {
            existingItem.setImageUrl(requestDto.getImageUrl());
        }
        if (requestDto.getServingSizeGrams() != null) {
            existingItem.setServingSizeGrams(requestDto.getServingSizeGrams());
        }
        if (requestDto.getCaloriesPerServing() != null) {
            existingItem.setCaloriesPerServing(requestDto.getCaloriesPerServing());
        }
        if (requestDto.getProteinsPerServing() != null) {
            existingItem.setProteinsPerServing(requestDto.getProteinsPerServing());
        }
        if (requestDto.getCarbohydratesPerServing() != null) {
            existingItem.setCarbohydratesPerServing(requestDto.getCarbohydratesPerServing());
        }
        if (requestDto.getFatsPerServing() != null) {
            existingItem.setFatsPerServing(requestDto.getFatsPerServing());
        }
        if (requestDto.getFiberPerServing() != null) {
            existingItem.setFiberPerServing(requestDto.getFiberPerServing());
        }
        if (requestDto.getSugarPerServing() != null) {
            existingItem.setSugarPerServing(requestDto.getSugarPerServing());
        }
        if (requestDto.getSodiumPerServing() != null) {
            existingItem.setSodiumPerServing(requestDto.getSodiumPerServing());
        }
        if (requestDto.getIsPopular() != null) {
            existingItem.setIsPopular(requestDto.getIsPopular());
        }
        if (requestDto.getIsActive() != null) {
            existingItem.setIsActive(requestDto.getIsActive());
        }
        
        existingItem.setUpdatedAt(LocalDateTime.now());
        
        FoodItem savedItem = foodItemRepository.save(existingItem);
        return convertToDto(savedItem);
    }
    
    @Override
    public void deleteFoodItem(Integer id) {
        if (!foodItemRepository.existsById(id)) {
            throw new RuntimeException("Alimento no encontrado con ID: " + id);
        }
        foodItemRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public FoodItemDto getFoodItemById(Integer id) {
        FoodItem foodItem = foodItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alimento no encontrado con ID: " + id));
        return convertToDto(foodItem);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultPage<FoodItemDto> getAllFoodItems(Pageable pageable) {
        Page<FoodItem> foodItemsPage = foodItemRepository.findAllByOrderByCreatedAtDesc(pageable);
        return convertToResultPage(foodItemsPage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultPage<FoodItemDto> searchFoodItems(String searchTerm, Pageable pageable) {
        Page<FoodItem> foodItemsPage = foodItemRepository.findByNameContainingIgnoreCaseOrderByCreatedAtDesc(searchTerm, pageable);
        return convertToResultPage(foodItemsPage);
    }
    
    @Override
    public FoodCategoryDto createFoodCategory(CreateFoodCategoryRequestDto requestDto) {
        FoodCategory category = new FoodCategory();
        category.setName(requestDto.getName());
        category.setDescription(requestDto.getDescription());
        category.setIcon(requestDto.getIcon());
        category.setIsActive(requestDto.getIsActive());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        FoodCategory savedCategory = foodCategoryRepository.save(category);
        return convertToDto(savedCategory);
    }
    
    @Override
    public FoodCategoryDto updateFoodCategory(Integer id, CreateFoodCategoryRequestDto requestDto) {
        FoodCategory existingCategory = foodCategoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        
        existingCategory.setName(requestDto.getName());
        existingCategory.setDescription(requestDto.getDescription());
        existingCategory.setIcon(requestDto.getIcon());
        existingCategory.setIsActive(requestDto.getIsActive());
        existingCategory.setUpdatedAt(LocalDateTime.now());
        
        FoodCategory savedCategory = foodCategoryRepository.save(existingCategory);
        return convertToDto(savedCategory);
    }
    
    @Override
    public void deleteFoodCategory(Integer id) {
        if (!foodCategoryRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada con ID: " + id);
        }
        
        // Check if category has food items
        long foodItemsCount = foodItemRepository.countByCategoryId(id);
        if (foodItemsCount > 0) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene " + foodItemsCount + " alimentos asociados");
        }
        
        foodCategoryRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultPage<FoodCategoryDto> getAllFoodCategories(Pageable pageable) {
        Page<FoodCategory> categoriesPage = foodCategoryRepository.findAllByOrderByNameAsc(pageable);
        return convertCategoryToResultPage(categoriesPage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public java.util.List<FoodCategoryDto> getAllFoodCategoriesNoPagination() {
        java.util.List<FoodCategory> categories = foodCategoryRepository.findAllByOrderByNameAsc();
        return categories.stream()
                .map(this::convertToDto)
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public AdminFoodStatsDto getFoodStats() {
        Long totalFoodItems = foodItemRepository.count();
        Long activeFoodItems = foodItemRepository.countByIsActive(true);
        Long inactiveFoodItems = foodItemRepository.countByIsActive(false);
        Long popularFoodItems = foodItemRepository.countByIsPopular(true);
        Long totalCategories = foodCategoryRepository.count();
        Long activeCategories = foodCategoryRepository.countByIsActive(true);
        
        return new AdminFoodStatsDto(totalFoodItems, activeFoodItems, inactiveFoodItems, 
                                   popularFoodItems, totalCategories, activeCategories);
    }
    
    // ============== PRIVATE HELPER METHODS ==============
    
    private FoodItemDto convertToDto(FoodItem foodItem) {
        MacroNutrientsDto macros = MacroNutrientsDto.builder()
                .proteins(foodItem.getProteinsPerServing())
                .carbohydrates(foodItem.getCarbohydratesPerServing())
                .fats(foodItem.getFatsPerServing())
                .calories(foodItem.getCaloriesPerServing())
                .fiber(foodItem.getFiberPerServing())
                .sugar(foodItem.getSugarPerServing())
                .sodium(foodItem.getSodiumPerServing())
                .build();

        return FoodItemDto.builder()
                .id(foodItem.getId())
                .name(foodItem.getName())
                .description(foodItem.getDescription())
                .category(foodItem.getCategory() != null ? foodItem.getCategory().getName() : null)
                .imageUrl(foodItem.getImageUrl())
                .servingSize(foodItem.getServingSizeGrams())
                .macros(macros)
                .popular(foodItem.getIsPopular())
                .isActive(foodItem.getIsActive())
                .build();
    }

    private FoodCategoryDto convertToDto(FoodCategory category) {
        return FoodCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .icon(category.getIcon())
                .isActive(category.getIsActive())
                .build();
    }
    
    private ResultPage<FoodItemDto> convertToResultPage(Page<FoodItem> foodsPage) {
        ResultPage<FoodItemDto> resultPage = new ResultPage<>();
        
        java.util.List<FoodItemDto> foodDtos = foodsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(java.util.stream.Collectors.toList());
        
        resultPage.setPagesResult(foodDtos);
        resultPage.setCurrentPage(foodsPage.getNumber());
        resultPage.setTotalItems(foodsPage.getTotalElements());
        resultPage.setTotalPages(foodsPage.getTotalPages());
        
        return resultPage;
    }
    
    private ResultPage<FoodCategoryDto> convertCategoryToResultPage(Page<FoodCategory> categoriesPage) {
        ResultPage<FoodCategoryDto> resultPage = new ResultPage<>();
        
        java.util.List<FoodCategoryDto> categoryDtos = categoriesPage.getContent().stream()
                .map(this::convertToDto)
                .collect(java.util.stream.Collectors.toList());
        
        resultPage.setPagesResult(categoryDtos);
        resultPage.setCurrentPage(categoriesPage.getNumber());
        resultPage.setTotalItems(categoriesPage.getTotalElements());
        resultPage.setTotalPages(categoriesPage.getTotalPages());
        
        return resultPage;
    }
} 