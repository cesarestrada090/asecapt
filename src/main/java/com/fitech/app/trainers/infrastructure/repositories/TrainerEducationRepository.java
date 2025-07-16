package com.fitech.app.trainers.infrastructure.repositories;

import com.fitech.app.trainers.domain.entities.TrainerEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerEducationRepository extends JpaRepository<TrainerEducation, Long> {
    
    /**
     * Find all education records for a specific trainer
     */
    @Query("SELECT te FROM TrainerEducation te WHERE te.trainerId = :trainerId ORDER BY te.year DESC")
    List<TrainerEducation> findByTrainerIdOrderByYearDesc(@Param("trainerId") Long trainerId);
    
    /**
     * Find education by trainer and type
     */
    @Query("SELECT te FROM TrainerEducation te WHERE te.trainerId = :trainerId AND te.type = :type ORDER BY te.year DESC")
    List<TrainerEducation> findByTrainerIdAndType(@Param("trainerId") Long trainerId, 
                                                 @Param("type") String type);
    
    /**
     * Find education by trainer and ID (for security validation)
     */
    @Query("SELECT te FROM TrainerEducation te WHERE te.id = :id AND te.trainerId = :trainerId")
    Optional<TrainerEducation> findByIdAndTrainerId(@Param("id") Long id, @Param("trainerId") Long trainerId);
    
    /**
     * Count education records for a trainer
     */
    @Query("SELECT COUNT(te) FROM TrainerEducation te WHERE te.trainerId = :trainerId")
    Long countByTrainerId(@Param("trainerId") Long trainerId);
    
    /**
     * Find recent education (last 5 years)
     */
    @Query("SELECT te FROM TrainerEducation te WHERE te.trainerId = :trainerId AND te.year >= :fromYear ORDER BY te.year DESC")
    List<TrainerEducation> findRecentEducationByTrainerId(@Param("trainerId") Long trainerId, @Param("fromYear") Integer fromYear);
    
    /**
     * Check if trainer has education
     */
    @Query("SELECT CASE WHEN COUNT(te) > 0 THEN true ELSE false END FROM TrainerEducation te WHERE te.trainerId = :trainerId")
    boolean existsByTrainerId(@Param("trainerId") Long trainerId);
    
    /**
     * Delete all education for a trainer (for cleanup operations)
     */
    void deleteByTrainerId(@Param("trainerId") Long trainerId);
} 