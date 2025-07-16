-- ==========================================
-- MIGRACIÓN: Sistema de Contratos de Servicios
-- ==========================================

-- ==========================================
-- TABLA: service_contracts (Contratos de servicios)
-- ==========================================
CREATE TABLE service_contracts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    trainer_id INT NOT NULL,
    service_id INT NOT NULL,
    contract_status ENUM('PENDING', 'ACTIVE', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    start_date DATE,
    end_date DATE,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    terms_accepted_at TIMESTAMP NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    FOREIGN KEY (service_id) REFERENCES trainer_services(id) ON DELETE CASCADE,
    
    -- Indexes para optimizar búsquedas
    INDEX idx_client_id (client_id),
    INDEX idx_trainer_id (trainer_id),
    INDEX idx_service_id (service_id),
    INDEX idx_contract_status (contract_status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_created_at (created_at),
    
    -- Índice compuesto para verificar contratos activos por cliente y servicio
    INDEX idx_client_service_status (client_id, service_id, contract_status)
);

-- ==========================================
-- COMENTARIOS DE TABLA
-- ==========================================
ALTER TABLE service_contracts COMMENT = 'Contratos de servicios entre clientes y trainers';

-- ==========================================
-- DATOS DE PRUEBA BÁSICOS
-- ==========================================
-- Insertar algunos contratos de prueba para testing
INSERT IGNORE INTO service_contracts (
    client_id, trainer_id, service_id, contract_status, 
    start_date, end_date, total_amount, payment_status, 
    terms_accepted_at, notes
)
VALUES 
-- Contrato activo para client1 con trainer1
((SELECT id FROM user WHERE username = 'client1' LIMIT 1),
 (SELECT id FROM user WHERE username = 'trainer1' LIMIT 1),
 (SELECT id FROM trainer_services WHERE trainer_id = (SELECT id FROM user WHERE username = 'trainer1' LIMIT 1) LIMIT 1),
 'ACTIVE', '2024-01-01', '2024-01-31',
 300.00, 'COMPLETED',
 NOW(), 'Contrato de entrenamiento personal básico'),

-- Contrato pendiente para client2 con trainer2
((SELECT id FROM user WHERE username = 'client2' LIMIT 1),
 (SELECT id FROM user WHERE username = 'trainer2' LIMIT 1),
 (SELECT id FROM trainer_services WHERE trainer_id = (SELECT id FROM user WHERE username = 'trainer2' LIMIT 1) LIMIT 1),
 'PENDING', '2024-02-01', '2024-02-28',
 250.00, 'PENDING',
 NOW(), 'Esperando confirmación del trainer'); 