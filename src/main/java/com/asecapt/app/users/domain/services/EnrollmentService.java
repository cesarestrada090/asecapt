package com.asecapt.app.users.domain.services;

import com.asecapt.app.users.domain.entities.Enrollment;
import com.asecapt.app.users.domain.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public List<Enrollment> getEnrollmentsByUser(Integer userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    public List<Enrollment> getEnrollmentsByProgram(Integer programId) {
        return enrollmentRepository.findByProgramId(programId);
    }

    public List<Enrollment> getCompletedEnrollments() {
        return enrollmentRepository.findByStatusOrderByCompletionDateDesc("completed");
    }

    public List<Enrollment> searchCompletedEnrollments(String query) {
        // For now, ignoring query parameter and returning all completed enrollments
        // TODO: Implement actual search functionality
        return enrollmentRepository.findByStatusOrderByCompletionDateDesc("completed");
    }

    public Optional<Enrollment> getEnrollmentById(Integer id) {
        return enrollmentRepository.findById(id);
    }

    public Optional<Enrollment> getEnrollmentByUserAndProgram(Integer userId, Integer programId) {
        return enrollmentRepository.findByUserIdAndProgramId(userId, programId);
    }

    public Enrollment createEnrollment(Integer userId, Integer programId, LocalDate startDate) {
        // Check if enrollment already exists
        Optional<Enrollment> existing = enrollmentRepository.findByUserIdAndProgramId(userId, programId);
        if (existing.isPresent()) {
            throw new RuntimeException("User is already enrolled in this program");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(userId);
        enrollment.setProgramId(programId);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setStartDate(startDate != null ? startDate : LocalDate.now());
        enrollment.setStatus("enrolled");

        return enrollmentRepository.save(enrollment);
    }

    public Enrollment updateEnrollmentStatus(Integer enrollmentId, String status) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        enrollment.setStatus(status);
        
        // If marking as completed, set completion date
        if ("completed".equals(status) && enrollment.getCompletionDate() == null) {
            enrollment.setCompletionDate(LocalDate.now());
        }

        return enrollmentRepository.save(enrollment);
    }

    public Enrollment updateGradeAndAttendance(Integer enrollmentId, BigDecimal finalGrade, BigDecimal attendancePercentage) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        enrollment.setFinalGrade(finalGrade);
        enrollment.setAttendancePercentage(attendancePercentage);

        return enrollmentRepository.save(enrollment);
    }

    public Enrollment updateEnrollment(Integer enrollmentId, com.asecapt.app.users.application.controllers.EnrollmentController.UpdateEnrollmentRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        // Update fields if provided
        if (request.getStatus() != null) {
            enrollment.setStatus(request.getStatus());
        }
        
        if (request.getFinalGrade() != null) {
            enrollment.setFinalGrade(request.getFinalGrade());
        }
        
        if (request.getAttendancePercentage() != null) {
            enrollment.setAttendancePercentage(request.getAttendancePercentage());
        }
        
        if (request.getCompletionDate() != null) {
            enrollment.setCompletionDate(request.getCompletionDate());
        }
        
        // If status is being set to completed and no completion date is provided, set it to today
        if ("completed".equals(request.getStatus()) && enrollment.getCompletionDate() == null) {
            enrollment.setCompletionDate(LocalDate.now());
        }

        return enrollmentRepository.save(enrollment);
    }

    public Enrollment completeEnrollment(Integer enrollmentId, BigDecimal finalGrade, BigDecimal attendancePercentage) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        enrollment.setStatus("completed");
        enrollment.setCompletionDate(LocalDate.now());
        enrollment.setFinalGrade(finalGrade);
        enrollment.setAttendancePercentage(attendancePercentage);

        return enrollmentRepository.save(enrollment);
    }

    /**
     * Delete enrollment by ID
     */
    public void deleteEnrollment(Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        // Physical deletion (complete removal from database)
        enrollmentRepository.delete(enrollment);
    }
} 