package com.fitech.app.trainers.infrastructure.repository;

import com.fitech.app.trainers.domain.entities.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Integer> {
    
    /**
     * Encuentra todos los tipos de servicios activos
     */
    List<ServiceType> findByIsActiveTrueOrderByName();
    
    /**
     * Encuentra un tipo de servicio por nombre
     */
    Optional<ServiceType> findByNameAndIsActiveTrue(String name);
    
    /**
     * Verifica si existe un tipo de servicio con el nombre dado
     */
    boolean existsByNameAndIsActiveTrue(String name);
    
    /**
     * Obtiene todos los tipos de servicios activos (query personalizada)
     */
    @Query("SELECT st FROM ServiceType st WHERE st.isActive = true ORDER BY st.name ASC")
    List<ServiceType> findAllActiveServiceTypes();
} 