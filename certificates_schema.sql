-- ========================================
-- ASECAPT - Certificate System Tables
-- MySQL Schema for QR Certificate Management
-- ========================================

-- Verificar que las tablas base existan antes de crear las foreign keys
-- Si no existen, este script fallará con un mensaje claro

-- Table: enrollment
-- Stores student enrollments in programs
CREATE TABLE IF NOT EXISTS enrollment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    program_id INT NOT NULL,
    enrollment_date DATE NOT NULL,
    start_date DATE,
    completion_date DATE,
    status ENUM('enrolled', 'in_progress', 'completed', 'suspended') DEFAULT 'enrolled',
    final_grade DECIMAL(4,2) DEFAULT NULL,
    attendance_percentage DECIMAL(5,2) DEFAULT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_enrollment (user_id, program_id),
    INDEX idx_user_id (user_id),
    INDEX idx_program_id (program_id),
    INDEX idx_status (status)
);

-- Table: certificate
-- Stores certificates issued for completed programs
CREATE TABLE IF NOT EXISTS certificate (
    id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL,
    certificate_number VARCHAR(50) UNIQUE NOT NULL,
    issue_date DATE NOT NULL,
    expiration_date DATE DEFAULT NULL,
    certificate_file_path VARCHAR(500) DEFAULT NULL,
    verification_token VARCHAR(100) UNIQUE NOT NULL,
    verification_url VARCHAR(500) NOT NULL,
    status ENUM('active', 'revoked', 'expired') DEFAULT 'active',
    issued_by_user_id INT DEFAULT NULL,
    scan_count INT DEFAULT 0,
    last_scanned_at TIMESTAMP NULL,
    revoked_at TIMESTAMP NULL,
    revoked_reason TEXT DEFAULT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_verification_token (verification_token),
    INDEX idx_certificate_number (certificate_number),
    INDEX idx_status (status),
    INDEX idx_enrollment_id (enrollment_id)
);

-- Table: certificate_validation
-- Logs every certificate validation/scan attempt
CREATE TABLE IF NOT EXISTS certificate_validation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    certificate_id INT NOT NULL,
    validation_token VARCHAR(100) NOT NULL,
    validated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    validator_ip VARCHAR(45) DEFAULT NULL,
    user_agent TEXT DEFAULT NULL,
    validation_result ENUM('valid', 'expired', 'revoked', 'not_found', 'invalid_token') NOT NULL,
    response_data JSON DEFAULT NULL,
    INDEX idx_certificate_id (certificate_id),
    INDEX idx_validation_result (validation_result),
    INDEX idx_validated_at (validated_at)
);

-- ========================================
-- ADD FOREIGN KEY CONSTRAINTS
-- (Only after ensuring base tables exist)
-- ========================================

-- Add foreign keys to enrollment table
ALTER TABLE enrollment 
ADD CONSTRAINT fk_enrollment_user 
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

ALTER TABLE enrollment 
ADD CONSTRAINT fk_enrollment_program 
    FOREIGN KEY (program_id) REFERENCES program(id) ON DELETE CASCADE;

-- Add foreign keys to certificate table  
ALTER TABLE certificate 
ADD CONSTRAINT fk_certificate_enrollment 
    FOREIGN KEY (enrollment_id) REFERENCES enrollment(id) ON DELETE CASCADE;

ALTER TABLE certificate 
ADD CONSTRAINT fk_certificate_issued_by 
    FOREIGN KEY (issued_by_user_id) REFERENCES user(id) ON DELETE SET NULL;

-- Add foreign keys to certificate_validation table
ALTER TABLE certificate_validation 
ADD CONSTRAINT fk_validation_certificate 
    FOREIGN KEY (certificate_id) REFERENCES certificate(id) ON DELETE CASCADE;

-- ========================================
-- SAMPLE DATA (OPTIONAL)
-- Uncomment and adjust user_id and program_id based on your existing data
-- ========================================

/*
-- Sample enrollments (adjust user_id and program_id based on existing data)
INSERT INTO enrollment (user_id, program_id, enrollment_date, start_date, completion_date, status, final_grade, attendance_percentage) VALUES
(1, 1, '2024-01-15', '2024-01-15', '2024-12-15', 'completed', 85.50, 95.00),
(2, 2, '2024-02-01', '2024-02-01', '2024-12-20', 'completed', 92.00, 98.50),
(3, 3, '2024-01-20', '2024-01-20', NULL, 'in_progress', NULL, 88.00),
(4, 1, '2024-03-10', '2024-03-10', '2024-11-30', 'completed', 78.25, 92.00);

-- Sample certificates for completed enrollments
INSERT INTO certificate (enrollment_id, certificate_number, issue_date, verification_token, verification_url, issued_by_user_id) VALUES
(1, 'ASECAPT-202412-0001', '2024-12-16', 'ASC-24-SOC-0001-VF7K', 'https://asecapt.com/verify/ASC-24-SOC-0001-VF7K', 1),
(2, 'ASECAPT-202412-0002', '2024-12-21', 'ASC-24-ARO-0002-MN8P', 'https://asecapt.com/verify/ASC-24-ARO-0002-MN8P', 1),
(4, 'ASECAPT-202411-0003', '2024-12-01', 'ASC-24-SOC-0003-QR9T', 'https://asecapt.com/verify/ASC-24-SOC-0003-QR9T', 1);

-- Sample validations
INSERT INTO certificate_validation (certificate_id, validation_token, validator_ip, user_agent, validation_result) VALUES
(1, 'ASC-24-SOC-0001-VF7K', '192.168.1.100', 'Mozilla/5.0 Mobile QR Scanner', 'valid'),
(1, 'ASC-24-SOC-0001-VF7K', '10.0.0.50', 'Chrome/120.0 Desktop', 'valid'),
(2, 'ASC-24-ARO-0002-MN8P', '172.16.0.25', 'Safari/17.0 iPhone', 'valid');
*/

-- ========================================
-- VERIFICATION QUERIES
-- Run these to verify the tables were created successfully
-- ========================================

-- Check if tables exist
-- SHOW TABLES LIKE '%enrollment%';
-- SHOW TABLES LIKE '%certificate%';

-- Check table structures
-- DESCRIBE enrollment;
-- DESCRIBE certificate;
-- DESCRIBE certificate_validation; 