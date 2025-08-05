package com.asecapt.app.users.application.controllers;

import com.asecapt.app.users.domain.entities.Certificate;
import com.asecapt.app.users.domain.services.CertificateService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {
    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    // Get all certificates
    @GetMapping
    public List<Certificate> getAllCertificates() {
        return certificateService.getAllCertificates();
    }

    // Get active certificates
    @GetMapping("/active")
    public List<Certificate> getActiveCertificates() {
        return certificateService.getActiveCertificates();
    }

    // Search certificates
    @GetMapping("/search")
    public List<Certificate> searchCertificates(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status) {
        return certificateService.searchCertificates(query, status);
    }

    // Get certificate by ID
    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getCertificateById(@PathVariable Integer id) {
        Optional<Certificate> certificate = certificateService.getCertificateById(id);
        return certificate.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    // Generate new certificate
    @PostMapping("/generate")
    public ResponseEntity<CertificateResponse> generateCertificate(@RequestBody GenerateCertificateRequest request) {
        try {
            Certificate certificate = certificateService.generateCertificate(
                request.getEnrollmentId(),
                request.getCertificateFilePath(),
                request.getIssuedByUserId()
            );

            // Generate QR code
            String qrCodeDataURL = certificateService.generateQRCodeDataURL(certificate.getVerificationUrl());

            CertificateResponse response = new CertificateResponse();
            response.setSuccess(true);
            response.setCertificate(certificate);
            response.setQrCodeDataURL(qrCodeDataURL);
            response.setMessage("Certificate generated successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CertificateResponse response = new CertificateResponse();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Upload certificate PDF
    @PostMapping("/upload-pdf")
    public ResponseEntity<Map<String, String>> uploadCertificatePDF(@RequestParam("certificate") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("error", "No file selected");
                return ResponseEntity.badRequest().body(response);
            }

            if (!"application/pdf".equals(file.getContentType())) {
                response.put("error", "Only PDF files are allowed");
                return ResponseEntity.badRequest().body(response);
            }

            // TODO: Implement actual file upload to server/cloud storage
            // For now, simulate file upload
            String fileName = "cert_" + System.currentTimeMillis() + ".pdf";
            String filePath = "/uploads/certificates/" + fileName;
            
            // In a real implementation, you would save the file:
            // Files.copy(file.getInputStream(), Paths.get(uploadDir + fileName));

            response.put("success", "true");
            response.put("filePath", filePath);
            response.put("fileName", fileName);
            response.put("message", "PDF uploaded successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Download QR code
    @GetMapping("/{id}/qr")
    public ResponseEntity<Map<String, String>> getQRCode(@PathVariable Integer id) {
        Optional<Certificate> certificateOpt = certificateService.getCertificateById(id);
        
        if (certificateOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Certificate certificate = certificateOpt.get();
        String qrCodeDataURL = certificateService.generateQRCodeDataURL(certificate.getVerificationUrl());

        Map<String, String> response = new HashMap<>();
        response.put("qrCodeDataURL", qrCodeDataURL);
        response.put("verificationUrl", certificate.getVerificationUrl());
        response.put("certificateNumber", certificate.getCertificateNumber());

        return ResponseEntity.ok(response);
    }

    // Revoke certificate
    @PutMapping("/{id}/revoke")
    public ResponseEntity<Certificate> revokeCertificate(
            @PathVariable Integer id,
            @RequestBody RevokeCertificateRequest request) {
        try {
            Certificate certificate = certificateService.revokeCertificate(
                id,
                request.getReason(),
                request.getRevokedByUserId()
            );
            return ResponseEntity.ok(certificate);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Reactivate certificate
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<Certificate> reactivateCertificate(@PathVariable Integer id) {
        try {
            Certificate certificate = certificateService.reactivateCertificate(id);
            return ResponseEntity.ok(certificate);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get certificate statistics
    @GetMapping("/stats")
    public ResponseEntity<CertificateStats> getCertificateStats() {
        CertificateStats stats = new CertificateStats();
        stats.setActiveCertificates(certificateService.getActiveCertificatesCount());
        stats.setRevokedCertificates(certificateService.getRevokedCertificatesCount());
        stats.setMostScanned(certificateService.getMostScannedCertificates());

        return ResponseEntity.ok(stats);
    }

    // DTOs
    public static class GenerateCertificateRequest {
        private Integer enrollmentId;
        private String certificateFilePath;
        private Integer issuedByUserId;

        // Getters and setters
        public Integer getEnrollmentId() { return enrollmentId; }
        public void setEnrollmentId(Integer enrollmentId) { this.enrollmentId = enrollmentId; }
        public String getCertificateFilePath() { return certificateFilePath; }
        public void setCertificateFilePath(String certificateFilePath) { this.certificateFilePath = certificateFilePath; }
        public Integer getIssuedByUserId() { return issuedByUserId; }
        public void setIssuedByUserId(Integer issuedByUserId) { this.issuedByUserId = issuedByUserId; }
    }

    public static class RevokeCertificateRequest {
        private String reason;
        private Integer revokedByUserId;

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public Integer getRevokedByUserId() { return revokedByUserId; }
        public void setRevokedByUserId(Integer revokedByUserId) { this.revokedByUserId = revokedByUserId; }
    }

    public static class CertificateResponse {
        private boolean success;
        private String message;
        private Certificate certificate;
        private String qrCodeDataURL;

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Certificate getCertificate() { return certificate; }
        public void setCertificate(Certificate certificate) { this.certificate = certificate; }
        public String getQrCodeDataURL() { return qrCodeDataURL; }
        public void setQrCodeDataURL(String qrCodeDataURL) { this.qrCodeDataURL = qrCodeDataURL; }
    }

    public static class CertificateStats {
        private Long activeCertificates;
        private Long revokedCertificates;
        private List<Certificate> mostScanned;

        // Getters and setters
        public Long getActiveCertificates() { return activeCertificates; }
        public void setActiveCertificates(Long activeCertificates) { this.activeCertificates = activeCertificates; }
        public Long getRevokedCertificates() { return revokedCertificates; }
        public void setRevokedCertificates(Long revokedCertificates) { this.revokedCertificates = revokedCertificates; }
        public List<Certificate> getMostScanned() { return mostScanned; }
        public void setMostScanned(List<Certificate> mostScanned) { this.mostScanned = mostScanned; }
    }
} 