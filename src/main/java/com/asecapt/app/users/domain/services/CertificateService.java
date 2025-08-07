package com.asecapt.app.users.domain.services;

import com.asecapt.app.users.domain.entities.Certificate;
import com.asecapt.app.users.domain.entities.Enrollment;
import com.asecapt.app.users.domain.repository.EnrollmentRepository;
import com.asecapt.app.users.infrastructure.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CertificateService {
    
    @Autowired
    private CertificateRepository certificateRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private QRCodeService qrCodeService;
    
    @Value("${app.certificates.upload-dir:certificates}")
    private String certificatesUploadDir;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    /**
     * Upload and create a certificate for a completed enrollment
     */
    public Certificate createCertificate(Integer enrollmentId, MultipartFile file, LocalDateTime issuedDate) throws IOException {
        System.out.println("Creating certificate for enrollment: " + enrollmentId);
        
        // Validate enrollment
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        if (!"completed".equals(enrollment.getStatus())) {
            throw new RuntimeException("Certificate can only be created for completed enrollments");
        }
        
        // Check if certificate already exists
        if (certificateRepository.existsByEnrollmentIdAndIsActiveTrue(enrollmentId)) {
            throw new RuntimeException("Certificate already exists for this enrollment");
        }
        
        // Generate unique certificate code
        String certificateCode = generateCertificateCode(enrollment);
        
        // Save file
        String fileName = saveFile(file, certificateCode);
        String filePath = Paths.get(certificatesUploadDir, fileName).toString();
        
        // Create certificate entity
        Certificate certificate = new Certificate(
            certificateCode, 
            enrollment, 
            filePath, 
            fileName, 
            issuedDate != null ? issuedDate : LocalDateTime.now()
        );
        
        // Save certificate
        certificate = certificateRepository.save(certificate);
        
        // Generate QR code
        String qrCodePath = qrCodeService.generateQRCode(
            buildCertificateUrl(certificateCode), 
            certificateCode
        );
        certificate.setQrCodePath(qrCodePath);
        
        System.out.println("Certificate created successfully: " + certificate.getCertificateCode());
        
        return certificateRepository.save(certificate);
    }
    
    /**
     * Get certificate by ID
     */
    public Optional<Certificate> getCertificateById(Integer certificateId) {
        return certificateRepository.findById(certificateId);
    }
    
    /**
     * Get certificate by code
     */
    public Optional<Certificate> getCertificateByCode(String certificateCode) {
        return certificateRepository.findByCertificateCode(certificateCode);
    }
    
    /**
     * Get all certificates for a student
     */
    public List<Certificate> getCertificatesByStudentId(Integer studentId) {
        return certificateRepository.findByStudentId(studentId);
    }
    
    /**
     * Get all certificates for a program
     */
    public List<Certificate> getCertificatesByProgramId(Integer programId) {
        return certificateRepository.findByProgramId(programId);
    }
    
    /**
     * Get all active certificates
     */
    public List<Certificate> getAllActiveCertificates() {
        return certificateRepository.findByIsActiveTrueOrderByIssuedDateDesc();
    }
    
    /**
     * Delete certificate (soft delete)
     */
    public void deleteCertificate(Integer certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
            .orElseThrow(() -> new RuntimeException("Certificate not found"));
        
        certificate.setIsActive(false);
        certificateRepository.save(certificate);
    }
    
    /**
     * Get certificate by enrollment ID
     */
    public Optional<Certificate> getCertificateByEnrollmentId(Integer enrollmentId) {
        return certificateRepository.findByEnrollmentId(enrollmentId);
    }
    
    /**
     * Get all certificates for a student by document number
     */
    public List<Certificate> getCertificatesByStudentDocument(String documentNumber) {
        return certificateRepository.findByStudentDocumentNumber(documentNumber);
    }
    
    /**
     * Generate unique certificate code
     */
    private String generateCertificateCode(Enrollment enrollment) {
        String programCode = enrollment.getProgram().getTitle().substring(0, 
            Math.min(3, enrollment.getProgram().getTitle().length())).toUpperCase();
        String studentCode = enrollment.getUser().getPerson().getDocumentNumber().substring(
            Math.max(0, enrollment.getUser().getPerson().getDocumentNumber().length() - 4)
        );
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        return String.format("CERT-%s-%s-%s-%s", programCode, studentCode, timestamp, uuid);
    }
    
    /**
     * Save uploaded file
     */
    private String saveFile(MultipartFile file, String certificateCode) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(certificatesUploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String fileName = certificateCode + fileExtension;
        Path filePath = uploadPath.resolve(fileName);
        
        // Save file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return fileName;
    }
    
    /**
     * Build certificate URL for QR code
     */
    private String buildCertificateUrl(String certificateCode) {
        return baseUrl + "/public/certificate/" + certificateCode;
    }
}