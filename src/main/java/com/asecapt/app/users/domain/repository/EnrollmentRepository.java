package com.asecapt.app.users.domain.repository;

import com.asecapt.app.users.domain.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    
    // Find enrollments by user ID
    List<Enrollment> findByUserId(Integer userId);
    
    // Find enrollments by program ID
    List<Enrollment> findByProgramId(Integer programId);
    
    // Find enrollments by status
    List<Enrollment> findByStatus(String status);
    
    // Find completed enrollments by user
    List<Enrollment> findByUserIdAndStatus(Integer userId, String status);
    
    // Find specific enrollment by user and program
    Optional<Enrollment> findByUserIdAndProgramId(Integer userId, Integer programId);
    
    // Find enrollments by status ordered by completion date (using Spring Data method naming)
    List<Enrollment> findByStatusOrderByCompletionDateDesc(String status);
} 