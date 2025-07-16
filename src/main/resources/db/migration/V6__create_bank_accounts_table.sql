-- Migration to create bank_accounts table
CREATE TABLE bank_accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    is_preferred BOOLEAN NOT NULL DEFAULT FALSE,
    account_holder_name VARCHAR(100) NOT NULL,
    swift_code VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_account (user_id, account_number)
); 