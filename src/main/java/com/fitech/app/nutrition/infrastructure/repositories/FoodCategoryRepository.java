package com.fitech.app.nutrition.infrastructure.repositories;

import com.fitech.app.nutrition.domain.entities.FoodCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Integer> {

    /**
     * Encuentra todas las categorías activas
     */
    List<FoodCategory> findByIsActiveTrueOrderByName();

    /**
     * Encuentra una categoría por nombre (activa)
     */
    Optional<FoodCategory> findByNameAndIsActiveTrue(String name);

    /**
     * Cuenta cuántas categorías activas existen
     */
    long countByIsActiveTrue();

    /**
     * Encuentra categorías que tienen alimentos asociados
     */
    @Query("SELECT DISTINCT fc FROM FoodCategory fc " +
           "JOIN fc.foodItems fi " +
           "WHERE fc.isActive = true AND fi.isActive = true")
    List<FoodCategory> findCategoriesWithActiveFoods();

    // ============== MÉTODOS ADMIN (AGREGAR ESTOS) ==============
    
    /**
     * Encuentra todas las categorías (activas e inactivas) ordenadas por nombre ASC - PARA ADMIN
     */
    Page<FoodCategory> findAllByOrderByNameAsc(Pageable pageable);
    
    /**
     * Encuentra todas las categorías (activas e inactivas) ordenadas por nombre ASC sin paginación - PARA ADMIN
     */
    List<FoodCategory> findAllByOrderByNameAsc();
    
    /**
     * Cuenta categorías por estado activo - PARA ADMIN STATS
     */
    long countByIsActive(boolean isActive);
} 