-- Crear tablas de membresías

-- Tabla de planes de membresía disponibles
CREATE TABLE membership_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,                                          -- ID único del plan
    name VARCHAR(100) NOT NULL,                                                    -- Nombre del plan (ej: "Plan Mensual", "Plan Anual")
    description TEXT,                                                              -- Descripción detallada del plan
    price DECIMAL(10,2) NOT NULL,                                                  -- Precio del plan
    currency VARCHAR(3) NOT NULL DEFAULT 'EUR',                                   -- Moneda del precio (EUR, USD, etc.)
    duration_days INT NOT NULL,                                                    -- Duración del plan en días
    billing_cycle ENUM('MONTHLY', 'QUARTERLY', 'SEMI_ANNUAL', 'ANNUAL', 'ONE_TIME') NOT NULL, -- Ciclo de facturación
    is_active BOOLEAN NOT NULL DEFAULT TRUE,                                      -- Si el plan está activo y disponible
    features TEXT,                                                                 -- Características del plan en formato JSON
    max_contracts INT,                                                             -- Máximo número de contratos simultáneos permitidos
    max_resources INT,                                                             -- Máximo número de recursos (dietas/rutinas) permitidos
    priority_support BOOLEAN NOT NULL DEFAULT FALSE,                              -- Si incluye soporte prioritario
    display_order INT NOT NULL DEFAULT 0,                                         -- Orden de visualización en el frontend
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                               -- Fecha de creación del plan
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    -- Fecha de última actualización
);

-- Tabla de membresías de usuarios (registra cada membresía activa o histórica)
CREATE TABLE user_memberships (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,                                          -- ID único de la membresía
    user_id INT NOT NULL,                                                          -- ID del usuario que tiene la membresía
    membership_type ENUM('PAYMENT', 'CONTRACT') NOT NULL,                          -- Tipo: por pago directo o por contrato con trainer
    status ENUM('ACTIVE', 'EXPIRED', 'CANCELLED', 'PENDING') NOT NULL,            -- Estado actual de la membresía
    start_date DATE NOT NULL,                                                      -- Fecha de inicio de la membresía
    end_date DATE NOT NULL,                                                        -- Fecha de fin de la membresía
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                               -- Fecha de creación del registro
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,   -- Fecha de última actualización
    
    -- Campos específicos para membresías por CONTRATO
    trainer_id INT,                                                                -- ID del trainer (solo para tipo CONTRACT)
    contract_details TEXT,                                                         -- Detalles del contrato en texto
    
    -- Campos específicos para membresías por PAGO
    plan_id BIGINT,                                                                -- ID del plan de membresía (solo para tipo PAYMENT)
    payment_amount DECIMAL(10,2),                                                  -- Monto pagado
    currency VARCHAR(3) DEFAULT 'EUR',                                            -- Moneda del pago
    auto_renewal BOOLEAN DEFAULT FALSE,                                            -- Si la membresía se renueva automáticamente
    
    -- Claves foráneas e índices
    FOREIGN KEY (plan_id) REFERENCES membership_plans(id),                        -- Referencia al plan de membresía
    INDEX idx_user_id (user_id),                                                  -- Índice para búsquedas por usuario
    INDEX idx_membership_type (membership_type),                                  -- Índice para filtrar por tipo
    INDEX idx_status (status),                                                    -- Índice para filtrar por estado
    INDEX idx_end_date (end_date),                                               -- Índice para encontrar membresías que expiran
    INDEX idx_trainer_id (trainer_id)                                            -- Índice para búsquedas por trainer
);

-- Tabla de pagos de membresías (historial de transacciones)
CREATE TABLE membership_payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,                                          -- ID único del pago
    membership_id BIGINT NOT NULL,                                                 -- ID de la membresía asociada
    user_id INT NOT NULL,                                                          -- ID del usuario que realizó el pago
    amount DECIMAL(10,2) NOT NULL,                                                 -- Monto del pago
    currency VARCHAR(3) NOT NULL DEFAULT 'EUR',                                   -- Moneda del pago
    payment_method ENUM('CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'BANK_TRANSFER', 'STRIPE', 'OTHER') NOT NULL, -- Método de pago utilizado
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED', 'CANCELLED') NOT NULL, -- Estado del pago
    transaction_id VARCHAR(100),                                                   -- ID de transacción del gateway de pago
    payment_gateway VARCHAR(50),                                                   -- Gateway utilizado (Stripe, PayPal, etc.)
    description TEXT,                                                              -- Descripción del pago
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                               -- Fecha de creación del pago
    processed_at TIMESTAMP NULL,                                                   -- Fecha de procesamiento (éxito o fallo)
    failure_reason TEXT,                                                           -- Razón del fallo si el pago falló
    
    -- Claves foráneas e índices
    FOREIGN KEY (membership_id) REFERENCES user_memberships(id),                  -- Referencia a la membresía
    INDEX idx_membership_id (membership_id),                                      -- Índice para búsquedas por membresía
    INDEX idx_user_id (user_id),                                                  -- Índice para búsquedas por usuario
    INDEX idx_status (status),                                                    -- Índice para filtrar por estado
    INDEX idx_transaction_id (transaction_id)                                     -- Índice para búsquedas por ID de transacción
);

-- Insertar datos iniciales básicos
INSERT INTO membership_plans (name, description, price, duration_days, billing_cycle, features, priority_support, display_order) VALUES
('Plan Mensual', 'Acceso premium mensual con todas las funcionalidades de FiTech', 9.99, 30, 'MONTHLY', '{}', FALSE, 1),
('Plan Anual', 'Acceso premium anual con descuento especial y soporte prioritario', 99.99, 365, 'ANNUAL', '{}', TRUE, 2);

-- Crear índices compuestos adicionales para optimización de consultas
CREATE INDEX idx_user_memberships_user_status ON user_memberships(user_id, status);        -- Búsquedas por usuario y estado
CREATE INDEX idx_user_memberships_trainer_status ON user_memberships(trainer_id, status);  -- Búsquedas por trainer y estado
CREATE INDEX idx_membership_payments_user_status ON membership_payments(user_id, status);  -- Pagos por usuario y estado 