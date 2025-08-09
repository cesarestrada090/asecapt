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
    
    @Autowired
    private S3CertificateService s3CertificateService;
    
    @Autowired
    private CourseInitialsService courseInitialsService;
    
    @Value("${app.base-url:${APP_BASE_URL:http://localhost:8080}}")
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
        
        // Validate that student has grade and attendance percentage
        if (enrollment.getFinalGrade() == null || enrollment.getFinalGrade().intValue() <= 0) {
            throw new RuntimeException("Cannot create certificate: Student must have a valid final grade");
        }
        
        if (enrollment.getAttendancePercentage() == null || enrollment.getAttendancePercentage().intValue() <= 0) {
            throw new RuntimeException("Cannot create certificate: Student must have a valid attendance percentage");
        }
        
        // Additional validation for minimum passing requirements
        if (enrollment.getFinalGrade().intValue() < 60) {
            throw new RuntimeException("Cannot create certificate: Student must have a passing grade (minimum 60)");
        }
        
        if (enrollment.getAttendancePercentage().intValue() < 80) {
            throw new RuntimeException("Cannot create certificate: Student must have minimum 80% attendance");
        }
        
        // Check if certificate already exists
        if (certificateRepository.existsByEnrollmentIdAndIsActiveTrue(enrollmentId)) {
            throw new RuntimeException("Certificate already exists for this enrollment");
        }
        
        // Generate unique certificate code
        String certificateCode = generateCertificateCode(enrollment);
        
        // Get student DNI and course initials for S3 organization
        String dni = enrollment.getUser().getPerson().getDocumentNumber();
        String courseTitle = enrollment.getProgram().getTitle();
        String courseInitials = courseInitialsService.generateCourseInitials(courseTitle);
        
        // Get file extension
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "pdf"; // default
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }
        
        // Upload certificate to S3
        String certificateS3Key = s3CertificateService.uploadCertificate(
            dni, 
            courseInitials, 
            certificateCode, 
            file.getBytes(), 
            fileExtension
        );
        
        // Generate QR code and upload to S3
        byte[] qrCodeBytes = qrCodeService.generateQRCodeBytes(
            buildCertificateUrl(certificateCode), 
            certificateCode
        );
        
        String qrS3Key = s3CertificateService.uploadQRCode(
            dni, 
            courseInitials, 
            certificateCode, 
            qrCodeBytes
        );
        
        // Create certificate entity with S3 paths
        Certificate certificate = new Certificate(
            certificateCode, 
            enrollment, 
            certificateS3Key,  // S3 key instead of local path
            generateFileName(courseInitials, certificateCode, fileExtension), 
            issuedDate != null ? issuedDate : LocalDateTime.now()
        );
        
        certificate.setQrCodePath(qrS3Key); // S3 key for QR code
        
        // Save certificate
        certificate = certificateRepository.save(certificate);
        
        System.out.println("✅ Certificate created successfully with S3 storage: " + certificate.getCertificateCode());
        System.out.println("📁 Certificate S3 Key: " + certificateS3Key);
        System.out.println("📱 QR Code S3 Key: " + qrS3Key);
        
        return certificate;
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
     * Build certificate URL for QR code
     */
    private String buildCertificateUrl(String certificateCode) {
        return baseUrl + "/public/certificate/" + certificateCode;
    }
    
    /**
     * Update certificate (for updating issue date, etc.)
     */
    public Certificate updateCertificate(Integer certificateId, com.asecapt.app.users.application.controllers.CertificateController.UpdateCertificateRequest request) {
        Certificate certificate = certificateRepository.findById(certificateId)
            .orElseThrow(() -> new RuntimeException("Certificate not found"));
        
        // Update issued date if provided
        if (request.getIssuedDate() != null && !request.getIssuedDate().trim().isEmpty()) {
            try {
                LocalDateTime issuedDate = LocalDateTime.parse(request.getIssuedDate() + "T00:00:00");
                certificate.setIssuedDate(issuedDate);
            } catch (Exception e) {
                throw new RuntimeException("Invalid date format. Expected YYYY-MM-DD");
            }
        }
        
        return certificateRepository.save(certificate);
    }
    
    /**

     * Generate file name for certificate
     */
    private String generateFileName(String courseInitials, String certificateCode, String fileExtension) {
        return String.format("%s-%s.%s", courseInitials, certificateCode, fileExtension);
    }
}