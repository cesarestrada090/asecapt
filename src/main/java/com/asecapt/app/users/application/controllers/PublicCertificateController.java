package com.asecapt.app.users.application.controllers;

import com.asecapt.app.users.domain.entities.Certificate;
import com.asecapt.app.users.domain.services.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/certificate")
@CrossOrigin(origins = "*")
public class PublicCertificateController {

    @Autowired
    private CertificateService certificateService;

    /**
     * Search certificates by student document number
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCertificatesByDocument(@RequestParam String documentNumber) {
        try {
            System.out.println("Searching certificates for document: " + documentNumber);
            
            var certificates = certificateService.getCertificatesByStudentDocument(documentNumber);
            
            if (certificates.isEmpty()) {
                return ResponseEntity.ok(createSearchErrorResponse("NO_CERTIFICATES_FOUND", "No se encontraron certificados para este documento"));
            }
            
            // Create response with certificates list
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("certificates", certificates.stream()
                .filter(Certificate::getIsActive) // Only active certificates
                .map(this::createCertificateInfo)
                .toArray());
            response.put("count", certificates.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error searching certificates: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(createSearchErrorResponse("INTERNAL_ERROR", "Error interno del servidor"));
        }
    }

    /**
     * Public endpoint to verify and display certificate information
     */
    @GetMapping("/{certificateCode}")
    public ResponseEntity<Map<String, Object>> verifyCertificate(@PathVariable String certificateCode) {
        try {
            System.out.println("Verifying certificate with code: " + certificateCode);
            
            Optional<Certificate> certificateOpt = certificateService.getCertificateByCode(certificateCode);
            
            if (certificateOpt.isEmpty()) {
                return ResponseEntity.ok(createErrorResponse("CERTIFICATE_NOT_FOUND", "Certificado no encontrado"));
            }
            
            Certificate certificate = certificateOpt.get();
            
            // Check if certificate is active
            if (!certificate.getIsActive()) {
                return ResponseEntity.ok(createErrorResponse("CERTIFICATE_INACTIVE", "Certificado inactivo o revocado"));
            }
            
            // Create response with certificate information
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("certificate", createCertificateInfo(certificate));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error verifying certificate: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(createErrorResponse("INTERNAL_ERROR", "Error interno del servidor"));
        }
    }
    
    /**
     * Create certificate information for public display
     */
    private Map<String, Object> createCertificateInfo(Certificate certificate) {
        Map<String, Object> certInfo = new HashMap<>();
        
        // Basic certificate info
        certInfo.put("id", certificate.getId());
        certInfo.put("certificateCode", certificate.getCertificateCode());
        certInfo.put("issuedDate", certificate.getIssuedDate());
        certInfo.put("createdAt", certificate.getCreatedAt());
        
        // Student information (from enrollment)
        if (certificate.getEnrollment() != null && certificate.getEnrollment().getUser() != null) {
            Map<String, Object> studentInfo = new HashMap<>();
            var user = certificate.getEnrollment().getUser();
            
            if (user.getPerson() != null) {
                studentInfo.put("firstName", user.getPerson().getFirstName());
                studentInfo.put("lastName", user.getPerson().getLastName());
                studentInfo.put("documentNumber", user.getPerson().getDocumentNumber());
                studentInfo.put("email", user.getPerson().getEmail());
            }
            
            certInfo.put("student", studentInfo);
        }
        
        // Program information (from enrollment)
        if (certificate.getEnrollment() != null && certificate.getEnrollment().getProgram() != null) {
            Map<String, Object> programInfo = new HashMap<>();
            var program = certificate.getEnrollment().getProgram();
            
            programInfo.put("title", program.getTitle());
            programInfo.put("description", program.getDescription());
            programInfo.put("duration", program.getDuration());
            programInfo.put("credits", program.getCredits());
            
            certInfo.put("program", programInfo);
        }
        
        // Enrollment information
        if (certificate.getEnrollment() != null) {
            Map<String, Object> enrollmentInfo = new HashMap<>();
            var enrollment = certificate.getEnrollment();
            
            enrollmentInfo.put("status", enrollment.getStatus());
            enrollmentInfo.put("enrollmentDate", enrollment.getCreatedAt());
            enrollmentInfo.put("completionDate", enrollment.getCompletionDate());
            enrollmentInfo.put("finalGrade", enrollment.getFinalGrade());
            enrollmentInfo.put("attendancePercentage", enrollment.getAttendancePercentage());
            
            certInfo.put("enrollment", enrollmentInfo);
        }
        
        return certInfo;
    }
    
    /**
     * Create error response for verification
     */
    private Map<String, Object> createErrorResponse(String errorCode, String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("valid", false);
        response.put("errorCode", errorCode);
        response.put("errorMessage", errorMessage);
        return response;
    }
    
    /**
     * Create error response for search
     */
    private Map<String, Object> createSearchErrorResponse(String errorCode, String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("errorCode", errorCode);
        response.put("errorMessage", errorMessage);
        return response;
    }
    
    /**
     * Public endpoint to download certificate file
     */
    @GetMapping("/download/{certificateId}")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable Integer certificateId) {
        try {
            Optional<Certificate> certificateOpt = certificateService.getCertificateById(certificateId);
            
            if (certificateOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Certificate certificate = certificateOpt.get();
            
            // Check if certificate is active
            if (!certificate.getIsActive()) {
                return ResponseEntity.notFound().build();
            }
            
            Path filePath = Paths.get(certificate.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + certificate.getFileName() + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}