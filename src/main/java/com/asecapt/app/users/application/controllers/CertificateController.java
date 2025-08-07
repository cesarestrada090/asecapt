package com.asecapt.app.users.application.controllers;

import com.asecapt.app.users.domain.entities.Certificate;
import com.asecapt.app.users.domain.services.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin(origins = "*")
public class CertificateController {
    
    @Autowired
    private CertificateService certificateService;
    
    /**
     * Upload and create certificate for an enrollment
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadCertificate(
            @RequestParam("enrollmentId") Integer enrollmentId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "issuedDate", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime issuedDate) {
        
        try {
            System.out.println("Uploading certificate for enrollment: " + enrollmentId);
            
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }
            
            // Validate file type (allow images and PDFs)
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
                return ResponseEntity.badRequest().body("Only image files (PNG, JPG, JPEG) and PDF files are allowed");
            }
            
            // Create certificate
            Certificate certificate = certificateService.createCertificate(enrollmentId, file, issuedDate);
            
            return ResponseEntity.ok(certificate);
            
        } catch (RuntimeException e) {
            System.err.println("Business error creating certificate: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error creating certificate: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error saving certificate file");
        } catch (Exception e) {
            System.err.println("Unexpected error creating certificate: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }
    
    /**
     * Get certificate by code
     */
    @GetMapping("/code/{certificateCode}")
    public ResponseEntity<Certificate> getCertificateByCode(@PathVariable String certificateCode) {
        Optional<Certificate> certificate = certificateService.getCertificateByCode(certificateCode);
        
        if (certificate.isPresent()) {
            return ResponseEntity.ok(certificate.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all certificates for a student
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Certificate>> getCertificatesByStudent(@PathVariable Integer studentId) {
        List<Certificate> certificates = certificateService.getCertificatesByStudentId(studentId);
        return ResponseEntity.ok(certificates);
    }
    
    /**
     * Get all certificates for a program
     */
    @GetMapping("/program/{programId}")
    public ResponseEntity<List<Certificate>> getCertificatesByProgram(@PathVariable Integer programId) {
        List<Certificate> certificates = certificateService.getCertificatesByProgramId(programId);
        return ResponseEntity.ok(certificates);
    }
    
    /**
     * Get certificate by enrollment ID
     */
    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<Certificate> getCertificateByEnrollment(@PathVariable Integer enrollmentId) {
        Optional<Certificate> certificate = certificateService.getCertificateByEnrollmentId(enrollmentId);
        
        if (certificate.isPresent()) {
            return ResponseEntity.ok(certificate.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all certificates
     */
    @GetMapping
    public ResponseEntity<List<Certificate>> getAllCertificates() {
        List<Certificate> certificates = certificateService.getAllActiveCertificates();
        return ResponseEntity.ok(certificates);
    }
    
    /**
     * Delete certificate
     */
    @DeleteMapping("/{certificateId}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable Integer certificateId) {
        try {
            certificateService.deleteCertificate(certificateId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Download certificate file
     */
    @GetMapping("/download/{certificateId}")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable Integer certificateId) {
        try {
            Optional<Certificate> certificateOpt = certificateService.getCertificateByCode(certificateId.toString());
            
            if (certificateOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Certificate certificate = certificateOpt.get();
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
    
    /**
     * Download QR code
     */
    @GetMapping("/qr/{certificateId}")
    public ResponseEntity<Resource> downloadQRCode(@PathVariable Integer certificateId) {
        try {
            Optional<Certificate> certificateOpt = certificateService.getCertificateByCode(certificateId.toString());
            
            if (certificateOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Certificate certificate = certificateOpt.get();
            
            if (certificate.getQrCodePath() == null) {
                return ResponseEntity.notFound().build();
            }
            
            Path qrPath = Paths.get(certificate.getQrCodePath());
            Resource resource = new UrlResource(qrPath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + certificate.getCertificateCode() + "_qr.png\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}