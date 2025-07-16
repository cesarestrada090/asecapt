-- Migration to update bank_accounts table to use person_id instead of user_id
-- First drop the existing table if it exists
DROP TABLE IF EXISTS bank_accounts;

-- Create bank_accounts table with person_id
CREATE TABLE bank_accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    person_id INT NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    is_preferred BOOLEAN NOT NULL DEFAULT FALSE,
    account_holder_name VARCHAR(100) NOT NULL,
    swift_code VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE,
    UNIQUE KEY unique_person_account (person_id, account_number)
); 