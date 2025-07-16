package com.fitech.app.nutrition.application.services;

import com.fitech.app.nutrition.application.dto.FoodCategoryDto;
import com.fitech.app.nutrition.application.dto.FoodItemDto;
import com.fitech.app.nutrition.application.dto.FoodSearchResponseDto;
import com.fitech.app.trainers.application.dto.MacroCalculationRequestDto;
import com.fitech.app.trainers.application.dto.MacroCalculationResponseDto;
import com.fitech.app.users.application.dto.ResultPage;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FoodService {
    
    /**
     * Obtiene todos los alimentos activos con categorías y populares
     */
    FoodSearchResponseDto getAllFoodsWithCategories();
    
    /**
     * Busca alimentos por término de búsqueda
     */
    List<FoodItemDto> searchFoods(String searchTerm);
    
    /**
     * Busca alimentos por categoría
     */
    List<FoodItemDto> getFoodsByCategory(Integer categoryId);
    
    /**
     * Busca alimentos por categoría y término
     */
    List<FoodItemDto> searchFoodsByCategoryAndTerm(Integer categoryId, String searchTerm);
    
    /**
     * Obtiene alimentos populares
     */
    List<FoodItemDto> getPopularFoods();
    
    /**
     * Obtiene todas las categorías activas
     */
    List<FoodCategoryDto> getAllCategories();
    
    /**
     * Obtiene un alimento por ID
     */
    Optional<FoodItemDto> getFoodById(Integer id);
    
    /**
     * Obtiene múltiples alimentos por IDs
     */
    List<FoodItemDto> getFoodsByIds(List<Integer> ids);
    
    /**
     * Calcula los macronutrientes totales de una lista de alimentos seleccionados
     */
    MacroCalculationResponseDto calculateMacros(MacroCalculationRequestDto request);
    
    // Métodos paginados
    
    /**
     * Obtiene todos los alimentos activos paginados
     */
    ResultPage<FoodItemDto> getAllFoodsPaginated(Pageable pageable);
    
    /**
     * Busca alimentos por término de búsqueda paginado
     */
    ResultPage<FoodItemDto> searchFoodsPaginated(String searchTerm, Pageable pageable);
    
    /**
     * Busca alimentos por categoría paginado
     */
    ResultPage<FoodItemDto> getFoodsByCategoryPaginated(Integer categoryId, Pageable pageable);
    
    /**
     * Busca alimentos por categoría y término paginado
     */
    ResultPage<FoodItemDto> searchFoodsByCategoryAndTermPaginated(Integer categoryId, String searchTerm, Pageable pageable);
} 