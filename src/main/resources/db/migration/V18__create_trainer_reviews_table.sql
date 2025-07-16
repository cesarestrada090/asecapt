-- Migration V18: Create trainer_reviews table
-- Purpose: Store client reviews and ratings for trainers

CREATE TABLE trainer_reviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    trainer_id INT NOT NULL,
    client_id INT NOT NULL,
    service_id INT,
    service_contract_id INT,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    is_anonymous BOOLEAN DEFAULT FALSE,
    trainer_response TEXT,
    trainer_response_date TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    CONSTRAINT fk_trainer_reviews_trainer FOREIGN KEY (trainer_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_trainer_reviews_client FOREIGN KEY (client_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_trainer_reviews_service FOREIGN KEY (service_id) REFERENCES trainer_services(id) ON DELETE SET NULL,
    CONSTRAINT fk_trainer_reviews_contract FOREIGN KEY (service_contract_id) REFERENCES service_contracts(id) ON DELETE SET NULL,
    
    -- Indexes for performance
    INDEX idx_trainer_reviews_trainer_id (trainer_id),
    INDEX idx_trainer_reviews_client_id (client_id),
    INDEX idx_trainer_reviews_service_contract_id (service_contract_id),
    INDEX idx_trainer_reviews_rating (rating),
    INDEX idx_trainer_reviews_created_at (created_at),
    
    -- Unique constraint: one review per client per contract
    UNIQUE KEY unique_client_contract_review (client_id, service_contract_id)
); 