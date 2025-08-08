package com.asecapt.app.users.application.controllers;

import com.asecapt.app.users.domain.entities.Enrollment;
import com.asecapt.app.users.domain.services.EnrollmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // Get all enrollments
    @GetMapping
    public List<Enrollment> getAllEnrollments() {
        return enrollmentService.getAllEnrollments();
    }

    // Get enrollments by user
    @GetMapping("/user/{userId}")
    public List<Enrollment> getEnrollmentsByUser(@PathVariable Integer userId) {
        return enrollmentService.getEnrollmentsByUser(userId);
    }

    // Get enrollments by program
    @GetMapping("/program/{programId}")
    public List<Enrollment> getEnrollmentsByProgram(@PathVariable Integer programId) {
        return enrollmentService.getEnrollmentsByProgram(programId);
    }

    // Get completed enrollments (for certificate generation)
    @GetMapping("/completed")
    public List<Enrollment> getCompletedEnrollments() {
        return enrollmentService.getCompletedEnrollments();
    }

    // Search completed enrollments
    @GetMapping("/completed/search")
    public List<Enrollment> searchCompletedEnrollments(@RequestParam String query) {
        return enrollmentService.searchCompletedEnrollments(query);
    }

    // Get enrollment summary for all active students (optimized endpoint)
    @GetMapping("/summary")
    public ResponseEntity<java.util.Map<Integer, EnrollmentService.EnrollmentSummary>> getEnrollmentSummary() {
        try {
            java.util.Map<Integer, EnrollmentService.EnrollmentSummary> summary = enrollmentService.getEnrollmentSummaryForActiveStudents();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get enrollment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Enrollment> getEnrollmentById(@PathVariable Integer id) {
        Optional<Enrollment> enrollment = enrollmentService.getEnrollmentById(id);
        return enrollment.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    // Create new enrollment
    @PostMapping
    public ResponseEntity<Enrollment> createEnrollment(@RequestBody CreateEnrollmentRequest request) {
        try {
            Enrollment enrollment = enrollmentService.createEnrollment(
                request.getUserId(),
                request.getProgramId(),
                request.getStartDate()
            );
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update enrollment status
    @PutMapping("/{id}/status")
    public ResponseEntity<Enrollment> updateEnrollmentStatus(
            @PathVariable Integer id,
            @RequestBody UpdateStatusRequest request) {
        try {
            Enrollment enrollment = enrollmentService.updateEnrollmentStatus(id, request.getStatus());
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update enrollment (full update)
    @PutMapping("/{id}")
    public ResponseEntity<Enrollment> updateEnrollment(
            @PathVariable Integer id,
            @RequestBody UpdateEnrollmentRequest request) {
        try {
            Enrollment enrollment = enrollmentService.updateEnrollment(id, request);
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Complete enrollment with grades
    @PutMapping("/{id}/complete")
    public ResponseEntity<Enrollment> completeEnrollment(
            @PathVariable Integer id,
            @RequestBody CompleteEnrollmentRequest request) {
        try {
            Enrollment enrollment = enrollmentService.completeEnrollment(
                id,
                request.getFinalGrade(),
                request.getAttendancePercentage()
            );
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete enrollment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Integer id) {
        try {
            enrollmentService.deleteEnrollment(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DTOs for request bodies
    public static class CreateEnrollmentRequest {
        private Integer userId;
        private Integer programId;
        private LocalDate startDate;

        // Getters and setters
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public Integer getProgramId() { return programId; }
        public void setProgramId(Integer programId) { this.programId = programId; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    }

    public static class UpdateStatusRequest {
        private String status;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class UpdateEnrollmentRequest {
        private String status;
        private BigDecimal finalGrade;
        private BigDecimal attendancePercentage;
        private LocalDate completionDate;
        private LocalDate enrollmentDate; // Add enrollmentDate field
        private String issueDate; // Add issueDate field for certificate

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public BigDecimal getFinalGrade() { return finalGrade; }
        public void setFinalGrade(BigDecimal finalGrade) { this.finalGrade = finalGrade; }
        public BigDecimal getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(BigDecimal attendancePercentage) { this.attendancePercentage = attendancePercentage; }
        public LocalDate getCompletionDate() { return completionDate; }
        public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }
        public LocalDate getEnrollmentDate() { return enrollmentDate; }
        public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
        public String getIssueDate() { return issueDate; }
        public void setIssueDate(String issueDate) { this.issueDate = issueDate; }
    }

    public static class CompleteEnrollmentRequest {
        private BigDecimal finalGrade;
        private BigDecimal attendancePercentage;

        public BigDecimal getFinalGrade() { return finalGrade; }
        public void setFinalGrade(BigDecimal finalGrade) { this.finalGrade = finalGrade; }
        public BigDecimal getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(BigDecimal attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    }
}
