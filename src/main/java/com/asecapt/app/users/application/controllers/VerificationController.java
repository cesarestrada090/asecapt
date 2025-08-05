package com.asecapt.app.users.application.controllers;

import com.asecapt.app.users.domain.entities.Certificate;
import com.asecapt.app.users.domain.services.CertificateService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/verify")
public class VerificationController {
    private final CertificateService certificateService;

    public VerificationController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    // Public certificate verification endpoint
    @GetMapping("/{token}")
    public ResponseEntity<CertificateVerificationResponse> verifyCertificate(
            @PathVariable String token,
            HttpServletRequest request) {
        
        // Extract client information
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        // Validate certificate and log the attempt
        Certificate certificate = certificateService.validateCertificate(token, clientIp, userAgent);

        CertificateVerificationResponse response = new CertificateVerificationResponse();

        if (certificate == null) {
            response.setValid(false);
            response.setStatus("not_found");
            response.setMessage("Certificado no encontrado o token inválido");
            return ResponseEntity.notFound().build();
        }

        // Build response based on certificate status
        switch (certificate.getStatus()) {
            case "active":
                response.setValid(true);
                response.setStatus("valid");
                response.setMessage("Certificado válido");
                response.setCertificateInfo(buildCertificateInfo(certificate));
                break;
            case "revoked":
                response.setValid(false);
                response.setStatus("revoked");
                response.setMessage("Certificado revocado");
                response.setRevokedAt(certificate.getRevokedAt());
                response.setRevokedReason(certificate.getRevokedReason());
                break;
            case "expired":
                response.setValid(false);
                response.setStatus("expired");
                response.setMessage("Certificado expirado");
                break;
            default:
                response.setValid(false);
                response.setStatus("invalid");
                response.setMessage("Estado de certificado inválido");
        }

        // Add verification metadata
        response.setVerificationToken(token);
        response.setVerifiedAt(java.time.LocalDateTime.now());
        response.setScanCount(certificate.getScanCount());

        return ResponseEntity.ok(response);
    }

    // Check certificate status without logging (for lightweight checks)
    @GetMapping("/{token}/status")
    public ResponseEntity<CertificateStatusResponse> getCertificateStatus(@PathVariable String token) {
        Certificate certificate = certificateService.getCertificateByVerificationToken(token).orElse(null);

        CertificateStatusResponse response = new CertificateStatusResponse();
        
        if (certificate == null) {
            response.setExists(false);
            response.setStatus("not_found");
        } else {
            response.setExists(true);
            response.setStatus(certificate.getStatus());
            response.setCertificateNumber(certificate.getCertificateNumber());
            response.setIssueDate(certificate.getIssueDate());
        }

        return ResponseEntity.ok(response);
    }

    // Helper methods
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null || xForwardedForHeader.isEmpty()) {
            return request.getRemoteAddr();
        } else {
            // X-Forwarded-For may contain multiple IPs, get the first one
            return xForwardedForHeader.split(",")[0].trim();
        }
    }

    private CertificateInfo buildCertificateInfo(Certificate certificate) {
        CertificateInfo info = new CertificateInfo();
        info.setCertificateNumber(certificate.getCertificateNumber());
        info.setIssueDate(certificate.getIssueDate());
        info.setExpirationDate(certificate.getExpirationDate());

        // TODO: Implement detailed enrollment info retrieval
        // For now, return minimal certificate info
        // In a complete implementation, we would:
        // 1. Use the enrollmentId to fetch enrollment details
        // 2. Use the enrollment's userId to fetch person details  
        // 3. Use the enrollment's programId to fetch program details
        
        // Create placeholder enrollment info
        Enrollment enrollmentInfo = new Enrollment();
        
        // Create placeholder student info
        Student studentInfo = new Student();
        studentInfo.setFullName("Estudiante Verificado"); // TODO: Fetch from database
        studentInfo.setDocumentNumber("********"); // TODO: Fetch from database
        studentInfo.setEmail("estudiante@asecapt.com"); // TODO: Fetch from database
        enrollmentInfo.setStudent(studentInfo);
        
        // Create placeholder program info
        Program programInfo = new Program();
        programInfo.setName("Programa Completado"); // TODO: Fetch from database
        programInfo.setCredits(48); // TODO: Fetch from database
        programInfo.setHours(1200); // TODO: Fetch from database
        programInfo.setDuration("12 meses"); // TODO: Fetch from database
        enrollmentInfo.setProgram(programInfo);
        
        // Set placeholder academic info
        enrollmentInfo.setStartDate(LocalDate.of(2024, 1, 15));
        enrollmentInfo.setCompletionDate(LocalDate.of(2024, 12, 15));
        enrollmentInfo.setFinalGrade(BigDecimal.valueOf(85.0));
        enrollmentInfo.setAttendancePercentage(BigDecimal.valueOf(95.0));
        
        info.setEnrollment(enrollmentInfo);

        return info;
    }

    // Response DTOs
    public static class CertificateVerificationResponse {
        private boolean valid;
        private String status;
        private String message;
        private String verificationToken;
        private java.time.LocalDateTime verifiedAt;
        private Integer scanCount;
        private CertificateInfo certificateInfo;
        private java.time.LocalDateTime revokedAt;
        private String revokedReason;

        // Getters and setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getVerificationToken() { return verificationToken; }
        public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }
        public java.time.LocalDateTime getVerifiedAt() { return verifiedAt; }
        public void setVerifiedAt(java.time.LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
        public Integer getScanCount() { return scanCount; }
        public void setScanCount(Integer scanCount) { this.scanCount = scanCount; }
        public CertificateInfo getCertificateInfo() { return certificateInfo; }
        public void setCertificateInfo(CertificateInfo certificateInfo) { this.certificateInfo = certificateInfo; }
        public java.time.LocalDateTime getRevokedAt() { return revokedAt; }
        public void setRevokedAt(java.time.LocalDateTime revokedAt) { this.revokedAt = revokedAt; }
        public String getRevokedReason() { return revokedReason; }
        public void setRevokedReason(String revokedReason) { this.revokedReason = revokedReason; }
    }

    public static class CertificateStatusResponse {
        private boolean exists;
        private String status;
        private String certificateNumber;
        private LocalDate issueDate;

        // Getters and setters
        public boolean isExists() { return exists; }
        public void setExists(boolean exists) { this.exists = exists; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getCertificateNumber() { return certificateNumber; }
        public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }
        public LocalDate getIssueDate() { return issueDate; }
        public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    }

    public static class CertificateInfo {
        private String certificateNumber;
        private LocalDate issueDate;
        private LocalDate expirationDate;
        private Enrollment enrollment;

        // Getters and setters
        public String getCertificateNumber() { return certificateNumber; }
        public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }
        public LocalDate getIssueDate() { return issueDate; }
        public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
        public LocalDate getExpirationDate() { return expirationDate; }
        public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
        public Enrollment getEnrollment() { return enrollment; }
        public void setEnrollment(Enrollment enrollment) { this.enrollment = enrollment; }
    }

    public static class Enrollment {
        private LocalDate startDate;
        private LocalDate completionDate;
        private BigDecimal finalGrade;
        private BigDecimal attendancePercentage;
        private Student student;
        private Program program;

        // Getters and setters
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getCompletionDate() { return completionDate; }
        public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }
        public BigDecimal getFinalGrade() { return finalGrade; }
        public void setFinalGrade(BigDecimal finalGrade) { this.finalGrade = finalGrade; }
        public BigDecimal getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(BigDecimal attendancePercentage) { this.attendancePercentage = attendancePercentage; }
        public Student getStudent() { return student; }
        public void setStudent(Student student) { this.student = student; }
        public Program getProgram() { return program; }
        public void setProgram(Program program) { this.program = program; }
    }

    public static class Student {
        private String fullName;
        private String documentNumber;
        private String email;

        // Getters and setters
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getDocumentNumber() { return documentNumber; }
        public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class Program {
        private String name;
        private Integer credits;
        private Integer hours;
        private String duration;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getCredits() { return credits; }
        public void setCredits(Integer credits) { this.credits = credits; }
        public Integer getHours() { return hours; }
        public void setHours(Integer hours) { this.hours = hours; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
    }
} 