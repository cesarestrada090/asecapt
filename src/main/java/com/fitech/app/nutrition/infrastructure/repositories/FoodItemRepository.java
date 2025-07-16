package com.fitech.app.nutrition.infrastructure.repositories;

import com.fitech.app.nutrition.domain.entities.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Integer> {

    /**
     * Encuentra todos los alimentos activos
     */
    List<FoodItem> findByIsActiveTrueOrderByName();

    /**
     * Encuentra alimentos populares y activos
     */
    List<FoodItem> findByIsPopularTrueAndIsActiveTrueOrderByName();

    /**
     * Encuentra alimentos por categoría
     */
    List<FoodItem> findByCategoryIdAndIsActiveTrueOrderByName(Integer categoryId);

    /**
     * Busca alimentos por nombre (contiene texto)
     */
    List<FoodItem> findByNameContainingIgnoreCaseAndIsActiveTrueOrderByName(String searchTerm);

    /**
     * Busca alimentos por descripción (contiene texto)
     */
    List<FoodItem> findByDescriptionContainingIgnoreCaseAndIsActiveTrueOrderByName(String searchTerm);

    /**
     * Busca alimentos por nombre o descripción en una categoría específica
     */
    List<FoodItem> findByCategoryIdAndNameContainingIgnoreCaseAndIsActiveTrueOrderByName(Integer categoryId, String searchTerm);

    /**
     * Busca alimentos por descripción en una categoría específica
     */
    List<FoodItem> findByCategoryIdAndDescriptionContainingIgnoreCaseAndIsActiveTrueOrderByName(Integer categoryId, String searchTerm);

    /**
     * Encuentra un alimento por ID si está activo
     */
    Optional<FoodItem> findByIdAndIsActiveTrue(Integer id);

    /**
     * Cuenta alimentos activos
     */
    long countByIsActiveTrue();

    /**
     * Encuentra alimentos por múltiples IDs - usando consulta simple
     */
    @Query("SELECT fi FROM FoodItem fi WHERE fi.id IN ?1 AND fi.isActive = true")
    List<FoodItem> findByIdsAndActive(List<Integer> ids);
    
    // Métodos paginados
    
    /**
     * Encuentra todos los alimentos activos paginados
     */
    Page<FoodItem> findByIsActiveTrueOrderByName(Pageable pageable);

    /**
     * Encuentra alimentos populares y activos paginados
     */
    Page<FoodItem> findByIsPopularTrueAndIsActiveTrueOrderByName(Pageable pageable);

    /**
     * Encuentra alimentos por categoría paginados
     */
    Page<FoodItem> findByCategoryIdAndIsActiveTrueOrderByName(Integer categoryId, Pageable pageable);

    /**
     * Busca alimentos por nombre (contiene texto) paginado
     */
    Page<FoodItem> findByNameContainingIgnoreCaseAndIsActiveTrueOrderByName(String searchTerm, Pageable pageable);

    /**
     * Busca alimentos por descripción (contiene texto) paginado
     */
    Page<FoodItem> findByDescriptionContainingIgnoreCaseAndIsActiveTrueOrderByName(String searchTerm, Pageable pageable);

    /**
     * Busca alimentos por nombre en una categoría específica paginado
     */
    Page<FoodItem> findByCategoryIdAndNameContainingIgnoreCaseAndIsActiveTrueOrderByName(Integer categoryId, String searchTerm, Pageable pageable);

    /**
     * Busca alimentos por descripción en una categoría específica paginado
     */
    Page<FoodItem> findByCategoryIdAndDescriptionContainingIgnoreCaseAndIsActiveTrueOrderByName(Integer categoryId, String searchTerm, Pageable pageable);

    // ============== MÉTODOS ADMIN (AGREGAR ESTOS) ==============
    
    /**
     * Encuentra todos los alimentos (activos e inactivos) ordenados por fecha de creación DESC - PARA ADMIN
     */
    Page<FoodItem> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Busca alimentos por nombre (incluyendo inactivos) ordenados por fecha de creación DESC - PARA ADMIN
     */
    Page<FoodItem> findByNameContainingIgnoreCaseOrderByCreatedAtDesc(String searchTerm, Pageable pageable);
    
    /**
     * Cuenta alimentos por estado activo - PARA ADMIN STATS
     */
    long countByIsActive(boolean isActive);
    
    /**
     * Cuenta alimentos populares - PARA ADMIN STATS  
     */
    long countByIsPopular(boolean isPopular);
    
    /**
     * Cuenta alimentos por categoría ID - PARA ADMIN (validar antes de eliminar categoría)
     */
    long countByCategoryId(Integer categoryId);
} 