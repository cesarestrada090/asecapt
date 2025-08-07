package com.asecapt.app.users.domain.repository;

import com.asecapt.app.users.domain.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    
    // Find all active enrollments (excluding soft-deleted)
    List<Enrollment> findByDeletedFalse();
    
    // Find enrollments by user ID (including soft-deleted for backward compatibility)
    List<Enrollment> findByUserId(Integer userId);
    
    // Find active enrollments by user ID (excluding soft-deleted)
    List<Enrollment> findByUserIdAndDeletedFalse(Integer userId);
    
    // Find enrollments by program ID (including soft-deleted for backward compatibility)
    List<Enrollment> findByProgramId(Integer programId);
    
    // Find active enrollments by program ID (excluding soft-deleted)
    List<Enrollment> findByProgramIdAndDeletedFalse(Integer programId);
    
    // Find enrollments by status (including soft-deleted for backward compatibility)
    List<Enrollment> findByStatus(String status);
    
    // Find active enrollments by status (excluding soft-deleted)
    List<Enrollment> findByStatusAndDeletedFalse(String status);
    
    // Find completed enrollments by user (including soft-deleted for backward compatibility)
    List<Enrollment> findByUserIdAndStatus(Integer userId, String status);
    
    // Find active completed enrollments by user (excluding soft-deleted)
    List<Enrollment> findByUserIdAndStatusAndDeletedFalse(Integer userId, String status);
    
    // Find specific enrollment by user and program (including soft-deleted - needed for reactivation)
    Optional<Enrollment> findByUserIdAndProgramId(Integer userId, Integer programId);
    
    // Find active enrollment by user and program (excluding soft-deleted)
    Optional<Enrollment> findByUserIdAndProgramIdAndDeletedFalse(Integer userId, Integer programId);
    
    // Find enrollments by status ordered by completion date (including soft-deleted for backward compatibility)
    List<Enrollment> findByStatusOrderByCompletionDateDesc(String status);
    
    // Find active enrollments by status ordered by completion date (excluding soft-deleted)
    List<Enrollment> findByStatusAndDeletedFalseOrderByCompletionDateDesc(String status);
} 