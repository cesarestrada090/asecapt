package com.fitech.app.trainers.infrastructure.repository;

import com.fitech.app.trainers.domain.entities.TrainerService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerServiceRepository extends JpaRepository<TrainerService, Integer> {
    
    List<TrainerService> findByTrainerIdAndIsActive(Integer trainerId, Boolean isActive);
    
    List<TrainerService> findByTrainerId(Integer trainerId);
    
    List<TrainerService> findByIsActive(Boolean isActive);
    
    @Query("SELECT ts FROM TrainerService ts WHERE ts.isActive = true AND ts.isInPerson = :isInPerson")
    List<TrainerService> findActiveServicesByType(@Param("isInPerson") Boolean isInPerson);
    
    @Query(value = "SELECT DISTINCT ts.* FROM trainer_services ts " +
                   "INNER JOIN service_districts sd ON ts.id = sd.service_id " +
                   "WHERE ts.is_active = true AND ts.is_in_person = true " +
                   "AND LOWER(sd.district_name) LIKE LOWER(CONCAT('%', :district, '%'))", 
           nativeQuery = true)
    List<TrainerService> findActiveServicesByDistrict(@Param("district") String district);
    
    @Query("SELECT COUNT(ts) FROM TrainerService ts WHERE ts.trainerId = :trainerId AND ts.isActive = true")
    Long countActiveServicesByTrainer(@Param("trainerId") Integer trainerId);
    
    /**
     * Verifica si ya existe un servicio del mismo tipo para el trainer
     */
    @Query("SELECT ts FROM TrainerService ts WHERE ts.trainerId = :trainerId AND ts.serviceType.id = :serviceTypeId")
    Optional<TrainerService> findByTrainerIdAndServiceTypeId(@Param("trainerId") Integer trainerId, @Param("serviceTypeId") Integer serviceTypeId);
    
    /**
     * Verifica si existe un servicio activo del mismo tipo para el trainer
     */
    @Query("SELECT ts FROM TrainerService ts WHERE ts.trainerId = :trainerId AND ts.serviceType.id = :serviceTypeId AND ts.isActive = true")
    Optional<TrainerService> findActiveByTrainerIdAndServiceTypeId(@Param("trainerId") Integer trainerId, @Param("serviceTypeId") Integer serviceTypeId);
} 