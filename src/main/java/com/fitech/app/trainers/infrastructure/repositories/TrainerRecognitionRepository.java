package com.fitech.app.trainers.infrastructure.repositories;

import com.fitech.app.trainers.domain.entities.TrainerRecognition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRecognitionRepository extends JpaRepository<TrainerRecognition, Long> {
    
    /**
     * Find all recognitions for a specific trainer
     */
    @Query("SELECT tr FROM TrainerRecognition tr WHERE tr.trainerId = :trainerId ORDER BY tr.date DESC")
    List<TrainerRecognition> findByTrainerIdOrderByDateDesc(@Param("trainerId") Long trainerId);
    
    /**
     * Find recognition by trainer and ID (for security validation)
     */
    @Query("SELECT tr FROM TrainerRecognition tr WHERE tr.id = :id AND tr.trainerId = :trainerId")
    Optional<TrainerRecognition> findByIdAndTrainerId(@Param("id") Long id, @Param("trainerId") Long trainerId);
    
    /**
     * Find recognitions by trainer and type
     */
    @Query("SELECT tr FROM TrainerRecognition tr WHERE tr.trainerId = :trainerId AND tr.type = :type ORDER BY tr.date DESC")
    List<TrainerRecognition> findByTrainerIdAndType(@Param("trainerId") Long trainerId, 
                                                   @Param("type") TrainerRecognition.RecognitionType type);
    
    /**
     * Find recognitions by trainer and level
     */
    @Query("SELECT tr FROM TrainerRecognition tr WHERE tr.trainerId = :trainerId AND tr.level = :level ORDER BY tr.date DESC")
    List<TrainerRecognition> findByTrainerIdAndLevel(@Param("trainerId") Long trainerId, 
                                                    @Param("level") TrainerRecognition.RecognitionLevel level);
    
    /**
     * Find recent recognitions (last 2 years)
     */
    @Query("SELECT tr FROM TrainerRecognition tr WHERE tr.trainerId = :trainerId " +
           "AND tr.date >= :fromDate ORDER BY tr.date DESC")
    List<TrainerRecognition> findRecentRecognitionsByTrainerId(@Param("trainerId") Long trainerId, 
                                                             @Param("fromDate") LocalDate fromDate);
    
    /**
     * Find awards (type = AWARD) for a trainer
     */
    @Query("SELECT tr FROM TrainerRecognition tr WHERE tr.trainerId = :trainerId " +
           "AND tr.type = 'AWARD' ORDER BY tr.date DESC")
    List<TrainerRecognition> findAwardsByTrainerId(@Param("trainerId") Long trainerId);
    
    /**
     * Find international level recognitions
     */
    @Query("SELECT tr FROM TrainerRecognition tr WHERE tr.trainerId = :trainerId " +
           "AND tr.level = 'INTERNACIONAL' ORDER BY tr.date DESC")
    List<TrainerRecognition> findInternationalRecognitionsByTrainerId(@Param("trainerId") Long trainerId);
    
    /**
     * Count recognitions for a trainer
     */
    @Query("SELECT COUNT(tr) FROM TrainerRecognition tr WHERE tr.trainerId = :trainerId")
    Long countByTrainerId(@Param("trainerId") Long trainerId);
    
    /**
     * Count recognitions by type for a trainer
     */
    @Query("SELECT COUNT(tr) FROM TrainerRecognition tr WHERE tr.trainerId = :trainerId AND tr.type = :type")
    Long countByTrainerIdAndType(@Param("trainerId") Long trainerId, 
                               @Param("type") TrainerRecognition.RecognitionType type);
    
    /**
     * Count recent recognitions (last 2 years)
     */
    @Query("SELECT COUNT(tr) FROM TrainerRecognition tr WHERE tr.trainerId = :trainerId " +
           "AND tr.date >= :fromDate")
    Long countRecentRecognitionsByTrainerId(@Param("trainerId") Long trainerId, 
                                          @Param("fromDate") LocalDate fromDate);
    
    /**
     * Check if trainer has recognitions
     */
    @Query("SELECT CASE WHEN COUNT(tr) > 0 THEN true ELSE false END FROM TrainerRecognition tr WHERE tr.trainerId = :trainerId")
    boolean existsByTrainerId(@Param("trainerId") Long trainerId);
    
    /**
     * Find top recognitions (awards and international level)
     */
    @Query("SELECT tr FROM TrainerRecognition tr WHERE tr.trainerId = :trainerId " +
           "AND (tr.type = 'AWARD' OR tr.level = 'INTERNACIONAL') " +
           "ORDER BY tr.date DESC")
    List<TrainerRecognition> findTopRecognitionsByTrainerId(@Param("trainerId") Long trainerId);
    
    /**
     * Delete all recognitions for a trainer (for cleanup operations)
     */
    void deleteByTrainerId(@Param("trainerId") Long trainerId);
} 