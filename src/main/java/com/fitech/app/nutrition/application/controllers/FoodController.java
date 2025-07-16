package com.fitech.app.nutrition.application.controllers;

import com.fitech.app.commons.application.controllers.BaseController;
import com.fitech.app.nutrition.application.dto.FoodCategoryDto;
import com.fitech.app.nutrition.application.dto.FoodItemDto;
import com.fitech.app.nutrition.application.dto.FoodSearchResponseDto;
import com.fitech.app.nutrition.application.services.FoodService;
import com.fitech.app.trainers.application.dto.MacroCalculationRequestDto;
import com.fitech.app.trainers.application.dto.MacroCalculationResponseDto;
import com.fitech.app.users.application.dto.ResultPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/app/nutrition/foods")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Foods", description = "API para gestión de alimentos y cálculo de macros")
@SecurityRequirement(name = "bearerAuth")
public class FoodController extends BaseController {

    private final FoodService foodService;

    @GetMapping
    @Operation(summary = "Obtener todos los alimentos con categorías", 
               description = "Devuelve todos los alimentos activos junto con sus categorías y alimentos populares")
    public ResponseEntity<FoodSearchResponseDto> getAllFoodsWithCategories() {
        log.info("GET /v1/app/nutrition/foods - Obteniendo todos los alimentos");
        
        FoodSearchResponseDto response = foodService.getAllFoodsWithCategories();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar alimentos", 
               description = "Busca alimentos por nombre o descripción")
    public ResponseEntity<List<FoodItemDto>> searchFoods(
            @Parameter(description = "Término de búsqueda")
            @RequestParam(required = false) String q) {
        log.info("GET /v1/app/nutrition/foods/search - Buscando alimentos con término: {}", q);
        
        List<FoodItemDto> foods = foodService.searchFoods(q);
        return ResponseEntity.ok(foods);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Obtener alimentos por categoría", 
               description = "Devuelve todos los alimentos de una categoría específica")
    public ResponseEntity<List<FoodItemDto>> getFoodsByCategory(
            @Parameter(description = "ID de la categoría")
            @PathVariable Integer categoryId) {
        log.info("GET /v1/app/nutrition/foods/category/{} - Obteniendo alimentos por categoría", categoryId);
        
        List<FoodItemDto> foods = foodService.getFoodsByCategory(categoryId);
        return ResponseEntity.ok(foods);
    }

    @GetMapping("/category/{categoryId}/search")
    @Operation(summary = "Buscar alimentos por categoría", 
               description = "Busca alimentos dentro de una categoría específica")
    public ResponseEntity<List<FoodItemDto>> searchFoodsByCategory(
            @Parameter(description = "ID de la categoría")
            @PathVariable Integer categoryId,
            @Parameter(description = "Término de búsqueda")
            @RequestParam(required = false) String q) {
        log.info("GET /v1/app/nutrition/foods/category/{}/search - Buscando en categoría con término: {}", categoryId, q);
        
        List<FoodItemDto> foods = foodService.searchFoodsByCategoryAndTerm(categoryId, q);
        return ResponseEntity.ok(foods);
    }

    @GetMapping("/popular")
    @Operation(summary = "Obtener alimentos populares", 
               description = "Devuelve los alimentos marcados como populares")
    public ResponseEntity<List<FoodItemDto>> getPopularFoods() {
        log.info("GET /v1/app/nutrition/foods/popular - Obteniendo alimentos populares");
        
        List<FoodItemDto> foods = foodService.getPopularFoods();
        return ResponseEntity.ok(foods);
    }

    @GetMapping("/categories")
    @Operation(summary = "Obtener todas las categorías", 
               description = "Devuelve todas las categorías de alimentos activas")
    public ResponseEntity<List<FoodCategoryDto>> getAllCategories() {
        log.info("GET /v1/app/nutrition/foods/categories - Obteniendo todas las categorías");
        
        List<FoodCategoryDto> categories = foodService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener alimento por ID", 
               description = "Devuelve un alimento específico por su ID")
    public ResponseEntity<FoodItemDto> getFoodById(
            @Parameter(description = "ID del alimento")
            @PathVariable Integer id) {
        log.info("GET /v1/app/nutrition/foods/{} - Obteniendo alimento por ID", id);
        
        return foodService.getFoodById(id)
                .map(food -> ResponseEntity.ok(food))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/batch")
    @Operation(summary = "Obtener múltiples alimentos por IDs", 
               description = "Devuelve múltiples alimentos especificados por sus IDs")
    public ResponseEntity<List<FoodItemDto>> getFoodsByIds(
            @Parameter(description = "Lista de IDs de alimentos")
            @RequestBody List<Integer> ids) {
        log.info("POST /v1/app/nutrition/foods/batch - Obteniendo alimentos por IDs: {}", ids);
        
        List<FoodItemDto> foods = foodService.getFoodsByIds(ids);
        return ResponseEntity.ok(foods);
    }

    @PostMapping("/calculate-macros")
    @Operation(summary = "Calcular macronutrientes", 
               description = "Calcula los macronutrientes totales de una lista de alimentos seleccionados con sus cantidades")
    public ResponseEntity<MacroCalculationResponseDto> calculateMacros(
            @Parameter(description = "Lista de alimentos seleccionados con sus cantidades")
            @RequestBody MacroCalculationRequestDto request) {
        log.info("POST /v1/app/nutrition/foods/calculate-macros - Calculando macros para {} alimentos", 
                request.getSelectedFoods().size());
        
        MacroCalculationResponseDto response = foodService.calculateMacros(request);
        return ResponseEntity.ok(response);
    }

    // Endpoints paginados
    
    @GetMapping("/paginated")
    @Operation(summary = "Obtener todos los alimentos paginados", 
               description = "Devuelve todos los alimentos activos con paginación")
    public ResponseEntity<Map<String, Object>> getAllFoodsPaginated(
            @Parameter(description = "Número de página (empezando en 1)")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /v1/app/nutrition/foods/paginated - página: {}, tamaño: {}", page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size);
        ResultPage<FoodItemDto> resultPage = foodService.getAllFoodsPaginated(pageable);
        Map<String, Object> response = prepareResponse(resultPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/paginated")
    @Operation(summary = "Buscar alimentos paginados", 
               description = "Busca alimentos por nombre o descripción con paginación")
    public ResponseEntity<Map<String, Object>> searchFoodsPaginated(
            @Parameter(description = "Término de búsqueda")
            @RequestParam(required = false) String q,
            @Parameter(description = "Número de página (empezando en 1)")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /v1/app/nutrition/foods/search/paginated - término: {}, página: {}, tamaño: {}", q, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size);
        ResultPage<FoodItemDto> resultPage = foodService.searchFoodsPaginated(q, pageable);
        Map<String, Object> response = prepareResponse(resultPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}/paginated")
    @Operation(summary = "Obtener alimentos por categoría paginados", 
               description = "Devuelve todos los alimentos de una categoría específica con paginación")
    public ResponseEntity<Map<String, Object>> getFoodsByCategoryPaginated(
            @Parameter(description = "ID de la categoría")
            @PathVariable Integer categoryId,
            @Parameter(description = "Número de página (empezando en 1)")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /v1/app/nutrition/foods/category/{}/paginated - página: {}, tamaño: {}", categoryId, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size);
        ResultPage<FoodItemDto> resultPage = foodService.getFoodsByCategoryPaginated(categoryId, pageable);
        Map<String, Object> response = prepareResponse(resultPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}/search/paginated")
    @Operation(summary = "Buscar alimentos por categoría paginados", 
               description = "Busca alimentos dentro de una categoría específica con paginación")
    public ResponseEntity<Map<String, Object>> searchFoodsByCategoryPaginated(
            @Parameter(description = "ID de la categoría")
            @PathVariable Integer categoryId,
            @Parameter(description = "Término de búsqueda")
            @RequestParam(required = false) String q,
            @Parameter(description = "Número de página (empezando en 1)")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /v1/app/nutrition/foods/category/{}/search/paginated - término: {}, página: {}, tamaño: {}", 
                categoryId, q, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size);
        ResultPage<FoodItemDto> resultPage = foodService.searchFoodsByCategoryAndTermPaginated(categoryId, q, pageable);
        Map<String, Object> response = prepareResponse(resultPage);
        return ResponseEntity.ok(response);
    }

    @Override
    protected String getResource() {
        return "foods";
    }
} 