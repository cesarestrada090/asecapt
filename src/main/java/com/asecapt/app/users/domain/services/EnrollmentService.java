package com.asecapt.app.users.domain.services;

import com.asecapt.app.users.domain.entities.Enrollment;
import com.asecapt.app.users.domain.entities.Certificate;
import com.asecapt.app.users.domain.repository.EnrollmentRepository;
import com.asecapt.app.users.infrastructure.repository.CertificateRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateRepository certificateRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, CertificateRepository certificateRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.certificateRepository = certificateRepository;
    }

    public List<Enrollment> getAllEnrollments() {
        // Return only active enrollments (not soft-deleted)
        return enrollmentRepository.findByDeletedFalse();
    }

    public List<Enrollment> getEnrollmentsByUser(Integer userId) {
        return enrollmentRepository.findByUserIdAndDeletedFalse(userId);
    }

    public List<Enrollment> getEnrollmentsByProgram(Integer programId) {
        return enrollmentRepository.findByProgramIdAndDeletedFalse(programId);
    }

    public List<Enrollment> getCompletedEnrollments() {
        return enrollmentRepository.findByStatusAndDeletedFalseOrderByCompletionDateDesc("completed");
    }

    public List<Enrollment> searchCompletedEnrollments(String query) {
        // For now, ignoring query parameter and returning all completed enrollments
        // TODO: Implement actual search functionality
        return enrollmentRepository.findByStatusAndDeletedFalseOrderByCompletionDateDesc("completed");
    }

    public Optional<Enrollment> getEnrollmentById(Integer id) {
        return enrollmentRepository.findById(id);
    }

    public Optional<Enrollment> getEnrollmentByUserAndProgram(Integer userId, Integer programId) {
        return enrollmentRepository.findByUserIdAndProgramId(userId, programId);
    }

    public Enrollment createEnrollment(Integer userId, Integer programId, LocalDate startDate) {
        // Check if active enrollment already exists (not deleted)
        Optional<Enrollment> existing = enrollmentRepository.findByUserIdAndProgramId(userId, programId);
        if (existing.isPresent() && !existing.get().getDeleted()) {
            throw new RuntimeException("User is already enrolled in this program");
        }
        
        // If there's a soft-deleted enrollment, reactivate it instead of creating new one
        if (existing.isPresent() && existing.get().getDeleted()) {
            Enrollment enrollment = existing.get();
            enrollment.setDeleted(false);
            enrollment.setDeletedAt(null);
            enrollment.setEnrollmentDate(LocalDate.now());
            enrollment.setStartDate(startDate != null ? startDate : LocalDate.now());
            enrollment.setStatus("enrolled");
            
            // Reset grades and completion data for fresh start
            enrollment.setFinalGrade(null);
            enrollment.setAttendancePercentage(null);
            enrollment.setCompletionDate(null);
            enrollment.setNotes(null);
            
            return enrollmentRepository.save(enrollment);
        }

        // Create new enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(userId);
        enrollment.setProgramId(programId);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setStartDate(startDate != null ? startDate : LocalDate.now());
        enrollment.setStatus("enrolled");
        enrollment.setDeleted(false);

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
        
        if (request.getEnrollmentDate() != null) {
            enrollment.setEnrollmentDate(request.getEnrollmentDate());
        }
        
        // If status is being set to completed and no completion date is provided, set it to today
        if ("completed".equals(request.getStatus()) && enrollment.getCompletionDate() == null) {
            enrollment.setCompletionDate(LocalDate.now());
        }

        // Save enrollment first
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);

        // Update certificate issue date if provided
        if (request.getIssueDate() != null && !request.getIssueDate().trim().isEmpty()) {
            try {
                // Find certificate associated with this enrollment
                Optional<Certificate> certificateOpt = certificateRepository.findByEnrollmentId(enrollmentId);
                if (certificateOpt.isPresent()) {
                    Certificate certificate = certificateOpt.get();
                    
                    // Parse and update the issued date
                    LocalDateTime issuedDate = LocalDateTime.parse(request.getIssueDate() + "T00:00:00");
                    certificate.setIssuedDate(issuedDate);
                    
                    // Save the updated certificate
                    certificateRepository.save(certificate);
                    
                    System.out.println("Updated certificate issue date for enrollment " + enrollmentId + " to " + request.getIssueDate());
                } else {
                    System.out.println("No certificate found for enrollment " + enrollmentId + ", cannot update issue date");
                }
            } catch (Exception e) {
                System.err.println("Error updating certificate issue date: " + e.getMessage());
                // Don't throw exception here, as enrollment update was successful
            }
        }

        return updatedEnrollment;
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
     * Soft delete enrollment by ID
     */
    public void deleteEnrollment(Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        // Soft delete: mark as deleted but keep in database for referential integrity
        enrollment.setDeleted(true);
        enrollment.setDeletedAt(java.time.LocalDateTime.now());
        
        enrollmentRepository.save(enrollment);
    }
}
