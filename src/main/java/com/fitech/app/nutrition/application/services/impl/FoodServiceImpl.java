package com.fitech.app.nutrition.application.services.impl;

import com.fitech.app.nutrition.application.dto.FoodCategoryDto;
import com.fitech.app.nutrition.application.dto.FoodItemDto;
import com.fitech.app.nutrition.application.dto.FoodSearchResponseDto;
import com.fitech.app.nutrition.application.dto.MacroNutrientsDto;
import com.fitech.app.nutrition.application.services.FoodService;
import com.fitech.app.nutrition.domain.entities.FoodCategory;
import com.fitech.app.nutrition.domain.entities.FoodItem;
import com.fitech.app.nutrition.infrastructure.repositories.FoodCategoryRepository;
import com.fitech.app.nutrition.infrastructure.repositories.FoodItemRepository;
import com.fitech.app.trainers.application.dto.MacroCalculationRequestDto;
import com.fitech.app.trainers.application.dto.MacroCalculationResponseDto;
import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.commons.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FoodServiceImpl implements FoodService {

    private final FoodItemRepository foodItemRepository;
    private final FoodCategoryRepository foodCategoryRepository;

    @Override
    public FoodSearchResponseDto getAllFoodsWithCategories() {
        log.info("Obteniendo todos los alimentos con categorías");
        
        List<FoodItemDto> foods = foodItemRepository.findByIsActiveTrueOrderByName()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        List<FoodCategoryDto> categories = foodCategoryRepository.findByIsActiveTrueOrderByName()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        List<FoodItemDto> popularFoods = foodItemRepository.findByIsPopularTrueAndIsActiveTrueOrderByName()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return FoodSearchResponseDto.builder()
                .foods(foods)
                .categories(categories)
                .popularFoods(popularFoods)
                .totalCount(foods.size())
                .build();
    }

    @Override
    public List<FoodItemDto> searchFoods(String searchTerm) {
        log.info("Buscando alimentos con término: {}", searchTerm);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return foodItemRepository.findByIsActiveTrueOrderByName()
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
        
        // Buscar por nombre y descripción por separado y combinar resultados
        List<FoodItem> nameResults = foodItemRepository.findByNameContainingIgnoreCaseAndIsActiveTrueOrderByName(searchTerm.trim());
        List<FoodItem> descriptionResults = foodItemRepository.findByDescriptionContainingIgnoreCaseAndIsActiveTrueOrderByName(searchTerm.trim());
        
        // Combinar y eliminar duplicados
        List<FoodItem> allResults = new java.util.ArrayList<>(nameResults);
        for (FoodItem item : descriptionResults) {
            if (!allResults.contains(item)) {
                allResults.add(item);
            }
        }
        
        return allResults.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FoodItemDto> getFoodsByCategory(Integer categoryId) {
        log.info("Obteniendo alimentos por categoría: {}", categoryId);
        
        return foodItemRepository.findByCategoryIdAndIsActiveTrueOrderByName(categoryId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FoodItemDto> searchFoodsByCategoryAndTerm(Integer categoryId, String searchTerm) {
        log.info("Buscando alimentos por categoría {} y término: {}", categoryId, searchTerm);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getFoodsByCategory(categoryId);
        }
        
        // Buscar por nombre y descripción en la categoría por separado y combinar resultados
        List<FoodItem> nameResults = foodItemRepository.findByCategoryIdAndNameContainingIgnoreCaseAndIsActiveTrueOrderByName(categoryId, searchTerm.trim());
        List<FoodItem> descriptionResults = foodItemRepository.findByCategoryIdAndDescriptionContainingIgnoreCaseAndIsActiveTrueOrderByName(categoryId, searchTerm.trim());
        
        // Combinar y eliminar duplicados
        List<FoodItem> allResults = new java.util.ArrayList<>(nameResults);
        for (FoodItem item : descriptionResults) {
            if (!allResults.contains(item)) {
                allResults.add(item);
            }
        }
        
        return allResults.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FoodItemDto> getPopularFoods() {
        log.info("Obteniendo alimentos populares");
        
        return foodItemRepository.findByIsPopularTrueAndIsActiveTrueOrderByName()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FoodCategoryDto> getAllCategories() {
        log.info("Obteniendo todas las categorías");
        
        return foodCategoryRepository.findByIsActiveTrueOrderByName()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FoodItemDto> getFoodById(Integer id) {
        log.info("Obteniendo alimento por ID: {}", id);
        
        return foodItemRepository.findByIdAndIsActiveTrue(id)
                .map(this::convertToDto);
    }

    @Override
    public List<FoodItemDto> getFoodsByIds(List<Integer> ids) {
        log.info("Obteniendo alimentos por IDs: {}", ids);
        
        return foodItemRepository.findByIdsAndActive(ids)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MacroCalculationResponseDto calculateMacros(MacroCalculationRequestDto request) {
        log.info("Calculando macros para {} alimentos", request.getSelectedFoods().size());
        
        // Obtener IDs de alimentos
        List<Integer> foodIds = request.getSelectedFoods().stream()
                .map(selectedFood -> selectedFood.getFoodId().intValue())
                .collect(Collectors.toList());
        
        // Obtener alimentos de la base de datos
        List<FoodItem> foods = foodItemRepository.findByIdsAndActive(foodIds);
        
        // Calcular macros para cada alimento
        List<MacroCalculationResponseDto.FoodCalculationDetailDto> foodDetails = new java.util.ArrayList<>();
        MacroNutrientsDto totalMacros = MacroNutrientsDto.builder()
                .proteins(BigDecimal.ZERO)
                .carbohydrates(BigDecimal.ZERO)
                .fats(BigDecimal.ZERO)
                .calories(BigDecimal.ZERO)
                .fiber(BigDecimal.ZERO)
                .sugar(BigDecimal.ZERO)
                .sodium(BigDecimal.ZERO)
                .build();
        
        for (MacroCalculationRequestDto.SelectedFoodDto selectedFood : request.getSelectedFoods()) {
            // Buscar el alimento correspondiente
            FoodItem food = foods.stream()
                    .filter(f -> f.getId().equals(selectedFood.getFoodId().intValue()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Alimento no encontrado: " + selectedFood.getFoodId()));
            
            // Calcular macros para esta cantidad
            Double quantity = selectedFood.getQuantity();
            Double totalWeight = food.getServingSizeGrams() * quantity;
            
            MacroNutrientsDto foodMacros = MacroNutrientsDto.builder()
                    .proteins(food.getProteinsPerServing().multiply(BigDecimal.valueOf(quantity)))
                    .carbohydrates(food.getCarbohydratesPerServing().multiply(BigDecimal.valueOf(quantity)))
                    .fats(food.getFatsPerServing().multiply(BigDecimal.valueOf(quantity)))
                    .calories(food.getCaloriesPerServing().multiply(BigDecimal.valueOf(quantity)))
                    .fiber(food.getFiberPerServing() != null ? food.getFiberPerServing().multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO)
                    .sugar(food.getSugarPerServing() != null ? food.getSugarPerServing().multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO)
                    .sodium(food.getSodiumPerServing() != null ? food.getSodiumPerServing().multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO)
                    .build();
            
            // Agregar al total
            totalMacros.setProteins(totalMacros.getProteins().add(foodMacros.getProteins()));
            totalMacros.setCarbohydrates(totalMacros.getCarbohydrates().add(foodMacros.getCarbohydrates()));
            totalMacros.setFats(totalMacros.getFats().add(foodMacros.getFats()));
            totalMacros.setCalories(totalMacros.getCalories().add(foodMacros.getCalories()));
            totalMacros.setFiber(totalMacros.getFiber().add(foodMacros.getFiber()));
            totalMacros.setSugar(totalMacros.getSugar().add(foodMacros.getSugar()));
            totalMacros.setSodium(totalMacros.getSodium().add(foodMacros.getSodium()));
            
            // Crear detalle del alimento
            MacroCalculationResponseDto.FoodCalculationDetailDto detail = 
                    new MacroCalculationResponseDto.FoodCalculationDetailDto(
                            selectedFood.getFoodId(),
                            food.getName(),
                            food.getCategory() != null ? food.getCategory().getName() : null,
                            quantity,
                            totalWeight,
                            foodMacros
                    );
            
            foodDetails.add(detail);
        }
        
        // Calcular porcentajes
        MacroCalculationResponseDto.MacroPercentagesDto percentages = calculatePercentages(totalMacros);
        
        return new MacroCalculationResponseDto(totalMacros, foodDetails, percentages);
    }
    
    private MacroCalculationResponseDto.MacroPercentagesDto calculatePercentages(MacroNutrientsDto totalMacros) {
        BigDecimal totalCalories = totalMacros.getCalories();
        
        if (totalCalories.equals(BigDecimal.ZERO)) {
            return new MacroCalculationResponseDto.MacroPercentagesDto(0.0, 0.0, 0.0);
        }
        
        // Calorías por gramo: proteínas=4, carbohidratos=4, grasas=9
        BigDecimal proteinCalories = totalMacros.getProteins().multiply(BigDecimal.valueOf(4));
        BigDecimal carbohydrateCalories = totalMacros.getCarbohydrates().multiply(BigDecimal.valueOf(4));
        BigDecimal fatCalories = totalMacros.getFats().multiply(BigDecimal.valueOf(9));
        
        Double proteinPercentage = proteinCalories.divide(totalCalories, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
        Double carbohydratePercentage = carbohydrateCalories.divide(totalCalories, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
        Double fatPercentage = fatCalories.divide(totalCalories, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
        
        return new MacroCalculationResponseDto.MacroPercentagesDto(
                proteinPercentage,
                carbohydratePercentage,
                fatPercentage
        );
    }

    // Métodos de conversión
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
    
    // Implementación de métodos paginados
    
    @Override
    public ResultPage<FoodItemDto> getAllFoodsPaginated(Pageable pageable) {
        log.info("Obteniendo todos los alimentos paginados - página: {}, tamaño: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<FoodItem> foodsPage = foodItemRepository.findByIsActiveTrueOrderByName(pageable);
        return convertToResultPage(foodsPage);
    }

    @Override
    public ResultPage<FoodItemDto> searchFoodsPaginated(String searchTerm, Pageable pageable) {
        log.info("Buscando alimentos paginados con término: {} - página: {}, tamaño: {}", 
                searchTerm, pageable.getPageNumber(), pageable.getPageSize());
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllFoodsPaginated(pageable);
        }
        
        // Para búsquedas complejas, necesitamos combinar resultados manualmente
        String trimmedTerm = searchTerm.trim();
        
        // Buscar por nombre
        Page<FoodItem> nameResults = foodItemRepository.findByNameContainingIgnoreCaseAndIsActiveTrueOrderByName(trimmedTerm, pageable);
        
        // Por ahora, solo devolvemos resultados por nombre para mantener la paginación simple
        // En una implementación más compleja, se podría usar una consulta JPQL custom
        return convertToResultPage(nameResults);
    }

    @Override
    public ResultPage<FoodItemDto> getFoodsByCategoryPaginated(Integer categoryId, Pageable pageable) {
        log.info("Obteniendo alimentos por categoría paginados - categoría: {}, página: {}, tamaño: {}", 
                categoryId, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<FoodItem> foodsPage = foodItemRepository.findByCategoryIdAndIsActiveTrueOrderByName(categoryId, pageable);
        return convertToResultPage(foodsPage);
    }

    @Override
    public ResultPage<FoodItemDto> searchFoodsByCategoryAndTermPaginated(Integer categoryId, String searchTerm, Pageable pageable) {
        log.info("Buscando alimentos por categoría y término paginados - categoría: {}, término: {}, página: {}, tamaño: {}", 
                categoryId, searchTerm, pageable.getPageNumber(), pageable.getPageSize());
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getFoodsByCategoryPaginated(categoryId, pageable);
        }
        
        // Por ahora, solo buscar por nombre para mantener la paginación simple
        String trimmedTerm = searchTerm.trim();
        Page<FoodItem> foodsPage = foodItemRepository.findByCategoryIdAndNameContainingIgnoreCaseAndIsActiveTrueOrderByName(
                categoryId, trimmedTerm, pageable);
        
        return convertToResultPage(foodsPage);
    }
    
    // Método auxiliar para convertir Page<FoodItem> a ResultPage<FoodItemDto>
    private ResultPage<FoodItemDto> convertToResultPage(Page<FoodItem> foodsPage) {
        ResultPage<FoodItemDto> resultPage = new ResultPage<>();
        
        List<FoodItemDto> foodDtos = foodsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        resultPage.setPagesResult(foodDtos);
        resultPage.setCurrentPage(foodsPage.getNumber());
        resultPage.setTotalItems(foodsPage.getTotalElements());
        resultPage.setTotalPages(foodsPage.getTotalPages());
        
        return resultPage;
    }
} 