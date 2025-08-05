package com.asecapt.app.users.domain.services;

import com.asecapt.app.users.domain.entities.Certificate;
import com.asecapt.app.users.domain.entities.Enrollment;
import com.asecapt.app.users.domain.entities.CertificateValidation;
import com.asecapt.app.users.domain.repository.CertificateRepository;
import com.asecapt.app.users.domain.repository.EnrollmentRepository;
import com.asecapt.app.users.domain.repository.CertificateValidationRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificateService {
    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateValidationRepository validationRepository;

    public CertificateService(CertificateRepository certificateRepository,
                             EnrollmentRepository enrollmentRepository,
                             CertificateValidationRepository validationRepository) {
        this.certificateRepository = certificateRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.validationRepository = validationRepository;
    }

    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    public List<Certificate> getActiveCertificates() {
        return certificateRepository.findByStatus("active");
    }

    public List<Certificate> searchCertificates(String query, String status) {
        // For now, ignoring query parameter and searching by status only
        // TODO: Implement actual text search functionality
        if (status == null || status.trim().isEmpty()) {
            status = "active";
        }
        return certificateRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    public Optional<Certificate> getCertificateById(Integer id) {
        return certificateRepository.findById(id);
    }

    public Optional<Certificate> getCertificateByVerificationToken(String token) {
        return certificateRepository.findByVerificationToken(token);
    }

    public Optional<Certificate> getCertificateByEnrollmentId(Integer enrollmentId) {
        return certificateRepository.findByEnrollmentId(enrollmentId);
    }

    public Certificate generateCertificate(Integer enrollmentId, String certificateFilePath, Integer issuedByUserId) {
        // Validate enrollment
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (!"completed".equals(enrollment.getStatus())) {
            throw new RuntimeException("Student has not completed the program");
        }

        // Check if certificate already exists
        Optional<Certificate> existingCert = certificateRepository.findByEnrollmentId(enrollmentId);
        if (existingCert.isPresent()) {
            throw new RuntimeException("Certificate already exists for this enrollment");
        }

        // Generate unique identifiers
        String certificateNumber = generateCertificateNumber();
        String verificationToken = generateVerificationToken();
        String verificationURL = generateVerificationURL(verificationToken);

        // Create certificate
        Certificate certificate = new Certificate();
        certificate.setEnrollmentId(enrollmentId);
        certificate.setCertificateNumber(certificateNumber);
        certificate.setIssueDate(LocalDate.now());
        certificate.setCertificateFilePath(certificateFilePath);
        certificate.setVerificationToken(verificationToken);
        certificate.setVerificationUrl(verificationURL);
        certificate.setStatus("active");
        certificate.setIssuedByUserId(issuedByUserId);

        return certificateRepository.save(certificate);
    }

    public Certificate revokeCertificate(Integer certificateId, String reason, Integer revokedByUserId) {
        Certificate certificate = certificateRepository.findById(certificateId)
            .orElseThrow(() -> new RuntimeException("Certificate not found"));

        certificate.setStatus("revoked");
        certificate.setRevokedAt(LocalDateTime.now());
        certificate.setRevokedReason(reason);

        return certificateRepository.save(certificate);
    }

    public Certificate reactivateCertificate(Integer certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
            .orElseThrow(() -> new RuntimeException("Certificate not found"));

        certificate.setStatus("active");
        certificate.setRevokedAt(null);
        certificate.setRevokedReason(null);

        return certificateRepository.save(certificate);
    }

    public Certificate validateCertificate(String verificationToken, String validatorIp, String userAgent) {
        Optional<Certificate> certOpt = certificateRepository.findByVerificationToken(verificationToken);
        
        String validationResult;
        Certificate certificate = null;

        if (certOpt.isEmpty()) {
            validationResult = "not_found";
        } else {
            certificate = certOpt.get();
            
            switch (certificate.getStatus()) {
                case "active":
                    validationResult = "valid";
                    // Update scan count
                    certificate.setScanCount(certificate.getScanCount() + 1);
                    certificate.setLastScannedAt(LocalDateTime.now());
                    certificateRepository.save(certificate);
                    break;
                case "revoked":
                    validationResult = "revoked";
                    break;
                case "expired":
                    validationResult = "expired";
                    break;
                default:
                    validationResult = "invalid_token";
            }
        }

        // Log validation attempt
        CertificateValidation validation = new CertificateValidation();
        validation.setCertificateId(certificate != null ? certificate.getId() : null);
        validation.setValidationToken(verificationToken);
        validation.setValidatorIp(validatorIp);
        validation.setUserAgent(userAgent);
        validation.setValidationResult(validationResult);
        validationRepository.save(validation);

        return certificate;
    }

    public String generateQRCodeDataURL(String verificationURL) {
        // TODO: Implement actual QR code generation using a library like ZXing
        // For now, return a placeholder
        return generateMockQRCode(verificationURL);
    }

    // Private helper methods
    private String generateCertificateNumber() {
        LocalDate now = LocalDate.now();
        String yearMonth = now.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String randomPart = String.format("%04d", (int)(Math.random() * 10000));
        return String.format("ASECAPT-%s-%s", yearMonth, randomPart);
    }

    private String generateVerificationToken() {
        LocalDate now = LocalDate.now();
        String yearSuffix = now.format(DateTimeFormatter.ofPattern("yy"));
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("ASC-%s-%s", yearSuffix, randomPart);
    }

    private String generateVerificationURL(String token) {
        // TODO: Get base URL from configuration
        return String.format("https://asecapt.com/verify/%s", token);
    }

    private String generateMockQRCode(String url) {
        // Generate a simple SVG QR pattern for demo purposes
        int size = 200;
        int squares = 120;
        StringBuilder qrPattern = new StringBuilder();
        qrPattern.append("<svg width=\"200\" height=\"200\" xmlns=\"http://www.w3.org/2000/svg\">");
        qrPattern.append("<rect width=\"200\" height=\"200\" fill=\"white\"/>");
        
        for (int i = 0; i < squares; i++) {
            int x = (int)(Math.random() * 20) * 10;
            int y = (int)(Math.random() * 20) * 10;
            qrPattern.append(String.format("<rect x=\"%d\" y=\"%d\" width=\"10\" height=\"10\" fill=\"black\"/>", x, y));
        }
        
        qrPattern.append("</svg>");
        
        return "data:image/svg+xml;base64," + java.util.Base64.getEncoder().encodeToString(qrPattern.toString().getBytes());
    }

    // Statistics methods
    public Long getActiveCertificatesCount() {
        return certificateRepository.countByStatus("active");
    }

    public Long getRevokedCertificatesCount() {
        return certificateRepository.countByStatus("revoked");
    }

    public List<Certificate> getMostScannedCertificates() {
        return certificateRepository.findByStatusOrderByScanCountDesc("active");
    }
} 