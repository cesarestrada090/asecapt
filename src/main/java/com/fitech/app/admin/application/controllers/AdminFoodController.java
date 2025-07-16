package com.fitech.app.admin.application.controllers;

import com.fitech.app.admin.application.dto.AdminFoodStatsDto;
import com.fitech.app.admin.application.dto.CreateFoodCategoryRequestDto;
import com.fitech.app.admin.application.dto.CreateFoodItemRequestDto;
import com.fitech.app.admin.application.dto.UpdateFoodItemRequestDto;
import com.fitech.app.admin.application.services.AdminFoodService;
import com.fitech.app.commons.application.controllers.BaseController;
import com.fitech.app.nutrition.application.dto.FoodCategoryDto;
import com.fitech.app.nutrition.application.dto.FoodItemDto;
import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.domain.entities.User;
import com.fitech.app.users.infrastructure.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/app/admin/nutrition")
@Tag(name = "Admin Food Management", description = "API para administradores para gestionar alimentos y categorías de la calculadora de macros")
@SecurityRequirement(name = "bearerAuth")
public class AdminFoodController extends BaseController {
    
    private final AdminFoodService adminFoodService;
    private final UserRepository userRepository;
    
    public AdminFoodController(AdminFoodService adminFoodService, UserRepository userRepository) {
        this.adminFoodService = adminFoodService;
        this.userRepository = userRepository;
    }
    
    // ============== FOOD ITEMS MANAGEMENT ==============
    
    @GetMapping("/foods")
    @Operation(summary = "Obtener todos los alimentos (Admin)", description = "Lista todos los alimentos con paginación para administradores")
    public ResponseEntity<Map<String, Object>> getAllFoodItems(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        validateAdminAccess();
        ResultPage<FoodItemDto> result = adminFoodService.getAllFoodItems(pageable);
        return ResponseEntity.ok(prepareResponse(result));
    }
    
    @GetMapping("/foods/search")
    @Operation(summary = "Buscar alimentos (Admin)", description = "Busca alimentos por nombre para administradores")
    public ResponseEntity<Map<String, Object>> searchFoodItems(
            @RequestParam String searchTerm,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        validateAdminAccess();
        ResultPage<FoodItemDto> result = adminFoodService.searchFoodItems(searchTerm, pageable);
        return ResponseEntity.ok(prepareResponse(result));
    }
    
    @GetMapping("/foods/{id}")
    @Operation(summary = "Obtener alimento por ID (Admin)", description = "Obtiene un alimento específico por su ID")
    public ResponseEntity<Map<String, Object>> getFoodItemById(@PathVariable Integer id) {
        validateAdminAccess();
        FoodItemDto result = adminFoodService.getFoodItemById(id);
        return ResponseEntity.ok(Map.of("food", result));
    }
    
    @PostMapping("/foods")
    @Operation(summary = "Crear alimento (Admin)", description = "Crea un nuevo alimento")
    public ResponseEntity<Map<String, Object>> createFoodItem(@Valid @RequestBody CreateFoodItemRequestDto requestDto) {
        Integer adminUserId = validateAdminAccess();
        FoodItemDto result = adminFoodService.createFoodItem(requestDto, adminUserId);
        return ResponseEntity.ok(Map.of(
            "message", "Alimento creado exitosamente",
            "food", result
        ));
    }
    
    @PutMapping("/foods/{id}")
    @Operation(summary = "Actualizar alimento (Admin)", description = "Actualiza un alimento existente")
    public ResponseEntity<Map<String, Object>> updateFoodItem(@PathVariable Integer id, 
                                          @Valid @RequestBody UpdateFoodItemRequestDto requestDto) {
        Integer adminUserId = validateAdminAccess();
        FoodItemDto result = adminFoodService.updateFoodItem(id, requestDto, adminUserId);
        return ResponseEntity.ok(Map.of(
            "message", "Alimento actualizado exitosamente",
            "food", result
        ));
    }
    
    @DeleteMapping("/foods/{id}")
    @Operation(summary = "Eliminar alimento (Admin)", description = "Elimina un alimento")
    public ResponseEntity<Map<String, Object>> deleteFoodItem(@PathVariable Integer id) {
        validateAdminAccess();
        adminFoodService.deleteFoodItem(id);
        return ResponseEntity.ok(Map.of("message", "Alimento eliminado exitosamente"));
    }
    
    // ============== FOOD CATEGORIES MANAGEMENT ==============
    
    @GetMapping("/categories")
    @Operation(summary = "Obtener todas las categorías (Admin)", description = "Lista todas las categorías de alimentos")
    public ResponseEntity<Map<String, Object>> getAllFoodCategories(
            @PageableDefault(size = 5, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        validateAdminAccess();
        ResultPage<FoodCategoryDto> result = adminFoodService.getAllFoodCategories(pageable);
        return ResponseEntity.ok(prepareResponse(result));
    }
    
    @GetMapping("/categories/all")
    @Operation(summary = "Obtener todas las categorías sin paginación (Admin)", description = "Lista todas las categorías de alimentos sin paginación")
    public ResponseEntity<Map<String, Object>> getAllFoodCategoriesNoPagination() {
        validateAdminAccess();
        java.util.List<FoodCategoryDto> categories = adminFoodService.getAllFoodCategoriesNoPagination();
        return ResponseEntity.ok(Map.of("categories", categories));
    }
    
    @PostMapping("/categories")
    @Operation(summary = "Crear categoría (Admin)", description = "Crea una nueva categoría de alimentos")
    public ResponseEntity<Map<String, Object>> createFoodCategory(@Valid @RequestBody CreateFoodCategoryRequestDto requestDto) {
        validateAdminAccess();
        FoodCategoryDto result = adminFoodService.createFoodCategory(requestDto);
        return ResponseEntity.ok(Map.of(
            "message", "Categoría creada exitosamente",
            "category", result
        ));
    }
    
    @PutMapping("/categories/{id}")
    @Operation(summary = "Actualizar categoría (Admin)", description = "Actualiza una categoría existente")
    public ResponseEntity<Map<String, Object>> updateFoodCategory(@PathVariable Integer id, 
                                              @Valid @RequestBody CreateFoodCategoryRequestDto requestDto) {
        validateAdminAccess();
        FoodCategoryDto result = adminFoodService.updateFoodCategory(id, requestDto);
        return ResponseEntity.ok(Map.of(
            "message", "Categoría actualizada exitosamente",
            "category", result
        ));
    }
    
    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Eliminar categoría (Admin)", description = "Elimina una categoría de alimentos")
    public ResponseEntity<Map<String, Object>> deleteFoodCategory(@PathVariable Integer id) {
        validateAdminAccess();
        adminFoodService.deleteFoodCategory(id);
        return ResponseEntity.ok(Map.of("message", "Categoría eliminada exitosamente"));
    }
    
    // ============== STATISTICS ==============
    
    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas (Admin)", description = "Obtiene estadísticas de alimentos y categorías")
    public ResponseEntity<Map<String, Object>> getFoodStats() {
        validateAdminAccess();
        AdminFoodStatsDto stats = adminFoodService.getFoodStats();
        return ResponseEntity.ok(Map.of("stats", stats));
    }
    
    // ============== HELPER METHODS ==============
    
    private Integer validateAdminAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Check if user is admin (type 3)
        if (user.getType() != 3) {
            throw new RuntimeException("Acceso denegado: solo administradores pueden gestionar alimentos");
        }
        
        return user.getId();
    }

    @Override
    protected String getResource() {
        return "data";
    }
} 