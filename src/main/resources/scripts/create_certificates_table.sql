-- Create certificates table
CREATE TABLE IF NOT EXISTS certificates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    certificate_code VARCHAR(100) UNIQUE NOT NULL,
    enrollment_id INT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    qr_code_path VARCHAR(500),
    issued_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Foreign key constraint
    CONSTRAINT fk_certificate_enrollment 
        FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) 
        ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- Indexes for better performance
    INDEX idx_certificate_code (certificate_code),
    INDEX idx_enrollment_id (enrollment_id),
    INDEX idx_issued_date (issued_date),
    INDEX idx_is_active (is_active)
);

-- Add comments for documentation
ALTER TABLE certificates COMMENT = 'Table to store student certificates and their QR codes';
ALTER TABLE certificates MODIFY COLUMN certificate_code VARCHAR(100) COMMENT 'Unique certificate identifier';
ALTER TABLE certificates MODIFY COLUMN enrollment_id INT COMMENT 'Reference to the enrollment that earned this certificate';
ALTER TABLE certificates MODIFY COLUMN file_path VARCHAR(500) COMMENT 'Path to the certificate file on disk';
ALTER TABLE certificates MODIFY COLUMN file_name VARCHAR(255) COMMENT 'Original filename of the certificate';
ALTER TABLE certificates MODIFY COLUMN qr_code_path VARCHAR(500) COMMENT 'Path to the generated QR code image';
ALTER TABLE certificates MODIFY COLUMN issued_date DATETIME COMMENT 'Date when the certificate was issued';
ALTER TABLE certificates MODIFY COLUMN is_active BOOLEAN COMMENT 'Soft delete flag - FALSE means deleted';