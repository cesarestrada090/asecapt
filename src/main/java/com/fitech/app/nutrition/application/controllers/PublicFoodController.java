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
@RequestMapping("/v1/app/public/nutrition/foods")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Public Foods", description = "API pública para la calculadora de macros - sin autenticación requerida")
public class PublicFoodController extends BaseController {

    private final FoodService foodService;

    // Endpoints básicos sin paginación
    
    @GetMapping
    @Operation(summary = "Obtener todos los alimentos con categorías (público)", 
               description = "Devuelve todos los alimentos activos junto con sus categorías y alimentos populares - acceso público")
    public ResponseEntity<FoodSearchResponseDto> getAllFoodsWithCategories() {
        log.info("GET /v1/app/public/nutrition/foods - Obteniendo todos los alimentos (público)");
        
        FoodSearchResponseDto response = foodService.getAllFoodsWithCategories();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar alimentos (público)", 
               description = "Busca alimentos por nombre o descripción - acceso público")
    public ResponseEntity<List<FoodItemDto>> searchFoods(
            @Parameter(description = "Término de búsqueda")
            @RequestParam(required = false) String q) {
        log.info("GET /v1/app/public/nutrition/foods/search - Buscando alimentos con término: {} (público)", q);
        
        List<FoodItemDto> foods = foodService.searchFoods(q);
        return ResponseEntity.ok(foods);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Obtener alimentos por categoría (público)", 
               description = "Devuelve todos los alimentos de una categoría específica - acceso público")
    public ResponseEntity<List<FoodItemDto>> getFoodsByCategory(
            @Parameter(description = "ID de la categoría")
            @PathVariable Integer categoryId) {
        log.info("GET /v1/app/public/nutrition/foods/category/{} - Obteniendo alimentos por categoría (público)", categoryId);
        
        List<FoodItemDto> foods = foodService.getFoodsByCategory(categoryId);
        return ResponseEntity.ok(foods);
    }

    @GetMapping("/category/{categoryId}/search")
    @Operation(summary = "Buscar alimentos por categoría (público)", 
               description = "Busca alimentos dentro de una categoría específica - acceso público")
    public ResponseEntity<List<FoodItemDto>> searchFoodsByCategory(
            @Parameter(description = "ID de la categoría")
            @PathVariable Integer categoryId,
            @Parameter(description = "Término de búsqueda")
            @RequestParam(required = false) String q) {
        log.info("GET /v1/app/public/nutrition/foods/category/{}/search - Buscando en categoría con término: {} (público)", categoryId, q);
        
        List<FoodItemDto> foods = foodService.searchFoodsByCategoryAndTerm(categoryId, q);
        return ResponseEntity.ok(foods);
    }

    @GetMapping("/popular")
    @Operation(summary = "Obtener alimentos populares (público)", 
               description = "Devuelve los alimentos marcados como populares - acceso público")
    public ResponseEntity<List<FoodItemDto>> getPopularFoods() {
        log.info("GET /v1/app/public/nutrition/foods/popular - Obteniendo alimentos populares (público)");
        
        List<FoodItemDto> foods = foodService.getPopularFoods();
        return ResponseEntity.ok(foods);
    }

    @GetMapping("/categories")
    @Operation(summary = "Obtener todas las categorías (público)", 
               description = "Devuelve todas las categorías de alimentos activas - acceso público")
    public ResponseEntity<List<FoodCategoryDto>> getAllCategories() {
        log.info("GET /v1/app/public/nutrition/foods/categories - Obteniendo todas las categorías (público)");
        
        List<FoodCategoryDto> categories = foodService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener alimento por ID (público)", 
               description = "Devuelve un alimento específico por su ID - acceso público")
    public ResponseEntity<FoodItemDto> getFoodById(
            @Parameter(description = "ID del alimento")
            @PathVariable Integer id) {
        log.info("GET /v1/app/public/nutrition/foods/{} - Obteniendo alimento por ID (público)", id);
        
        return foodService.getFoodById(id)
                .map(food -> ResponseEntity.ok(food))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/calculate-macros")
    @Operation(summary = "Calcular macronutrientes (público)", 
               description = "Calcula los macronutrientes totales de una lista de alimentos seleccionados con sus cantidades - acceso público")
    public ResponseEntity<MacroCalculationResponseDto> calculateMacros(
            @Parameter(description = "Lista de alimentos seleccionados con sus cantidades")
            @RequestBody MacroCalculationRequestDto request) {
        log.info("POST /v1/app/public/nutrition/foods/calculate-macros - Calculando macros para {} alimentos (público)", 
                request.getSelectedFoods().size());
        
        MacroCalculationResponseDto response = foodService.calculateMacros(request);
        return ResponseEntity.ok(response);
    }

    // Endpoints paginados
    
    @GetMapping("/paginated")
    @Operation(summary = "Obtener todos los alimentos paginados (público)", 
               description = "Devuelve todos los alimentos activos con paginación - acceso público")
    public ResponseEntity<Map<String, Object>> getAllFoodsPaginated(
            @Parameter(description = "Número de página (empezando en 1)")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /v1/app/public/nutrition/foods/paginated - página: {}, tamaño: {} (público)", page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size);
        ResultPage<FoodItemDto> resultPage = foodService.getAllFoodsPaginated(pageable);
        Map<String, Object> response = prepareResponse(resultPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/paginated")
    @Operation(summary = "Buscar alimentos paginados (público)", 
               description = "Busca alimentos por nombre o descripción con paginación - acceso público")
    public ResponseEntity<Map<String, Object>> searchFoodsPaginated(
            @Parameter(description = "Término de búsqueda")
            @RequestParam(required = false) String q,
            @Parameter(description = "Número de página (empezando en 1)")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /v1/app/public/nutrition/foods/search/paginated - término: {}, página: {}, tamaño: {} (público)", q, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size);
        ResultPage<FoodItemDto> resultPage = foodService.searchFoodsPaginated(q, pageable);
        Map<String, Object> response = prepareResponse(resultPage);
        return ResponseEntity.ok(response);
    }

    @Override
    protected String getResource() {
        return "foods";
    }
} 