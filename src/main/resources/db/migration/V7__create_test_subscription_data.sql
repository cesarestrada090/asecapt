-- V7: Crear datos de prueba para suscripciones
-- Insertar membresías de prueba para usuarios existentes

-- Insertar membresía de pago para usuario 1 (premium por pago)
INSERT INTO user_memberships (user_id, membership_type, status, start_date, end_date, plan_id, payment_amount, currency, auto_renewal, created_at, updated_at)
VALUES (1, 'PAYMENT', 'ACTIVE', '2024-01-15', '2024-02-15', 1, 29.99, 'EUR', true, NOW(), NOW());

-- Insertar membresía por contrato para usuario 2 (premium por contrato)  
INSERT INTO user_memberships (user_id, membership_type, status, start_date, end_date, trainer_id, contract_details, created_at, updated_at)
VALUES (2, 'CONTRACT', 'ACTIVE', '2024-01-10', '2024-04-10', 3, 'Contrato de entrenamiento personalizado por 3 meses', NOW(), NOW());

-- Insertar algunos pagos de prueba para el usuario 1
INSERT INTO membership_payments (membership_id, user_id, amount, currency, status, payment_method, processed_at, transaction_id, created_at, updated_at)
VALUES 
(1, 1, 29.99, 'EUR', 'COMPLETED', 'CREDIT_CARD', '2024-01-15 10:30:00', 'TXN_001_2024', NOW(), NOW()),
(1, 1, 29.99, 'EUR', 'COMPLETED', 'CREDIT_CARD', '2023-12-15 10:30:00', 'TXN_002_2023', NOW(), NOW()),
(1, 1, 29.99, 'EUR', 'COMPLETED', 'CREDIT_CARD', '2023-11-15 10:30:00', 'TXN_003_2023', NOW(), NOW());

-- Insertar membresía expirada para usuario 3
INSERT INTO user_memberships (user_id, membership_type, status, start_date, end_date, plan_id, payment_amount, currency, auto_renewal, created_at, updated_at)
VALUES (3, 'PAYMENT', 'EXPIRED', '2023-11-01', '2023-12-01', 1, 29.99, 'EUR', false, NOW(), NOW());

-- Insertar pago fallido para usuario 3
INSERT INTO membership_payments (membership_id, user_id, amount, currency, status, payment_method, processed_at, failure_reason, created_at, updated_at)
VALUES 
(3, 3, 29.99, 'EUR', 'FAILED', 'CREDIT_CARD', '2023-12-01 10:30:00', 'Tarjeta de crédito expirada', NOW(), NOW()); 