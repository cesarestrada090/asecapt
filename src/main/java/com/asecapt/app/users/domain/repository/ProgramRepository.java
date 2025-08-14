package com.asecapt.app.users.domain.repository;

import com.asecapt.app.users.domain.entities.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Integer> {
    
    // Search methods
    List<Program> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
    List<Program> findByTitleContainingIgnoreCase(String title);
    List<Program> findByDescriptionContainingIgnoreCase(String description);
    
    // Filter by type and status
    List<Program> findByType(String type);
    List<Program> findByStatus(String status);
    List<Program> findByTypeAndStatus(String type, String status);
    
    // Find by category
    List<Program> findByCategory(String category);
    List<Program> findByCategoryContainingIgnoreCase(String category);
    
    // Find by instructor
    List<Program> findByInstructor(String instructor);
    List<Program> findByInstructorContainingIgnoreCase(String instructor);
    
    // Order by date
    List<Program> findAllByOrderByCreatedAtDesc();
    List<Program> findByStatusOrderByCreatedAtDesc(String status);
    
    // Count methods
    Long countByStatus(String status);
    Long countByType(String type);
    Long countByCategory(String category);
    
    // Landing page specific methods
    List<Program> findByShowInLandingTrue();
    List<Program> findByShowInLandingTrueAndStatus(String status);
    List<Program> findByShowInLandingTrueOrderByCreatedAtDesc();
    List<Program> findByShowInLandingTrueAndStatusOrderByCreatedAtDesc(String status);
}
