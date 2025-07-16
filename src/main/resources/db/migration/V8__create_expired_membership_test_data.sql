-- V8: Crear datos de prueba con membresía expirada para probar renovación

-- Actualizar la membresía del usuario 3 para que esté expirada (para pruebas de renovación)
UPDATE user_memberships 
SET status = 'EXPIRED', 
    start_date = '2023-10-01', 
    end_date = '2023-11-01',
    updated_at = NOW()
WHERE user_id = 3;

-- Asegurar que el usuario 3 sea premium pero con membresía expirada
UPDATE users 
SET is_premium = true, 
    premium_by = 'PAYMENT',
    updated_at = NOW()
WHERE id = 3;

-- Crear un usuario adicional (usuario 4) con membresía expirada para más pruebas
INSERT INTO user_memberships (user_id, membership_type, status, start_date, end_date, plan_id, payment_amount, currency, auto_renewal, created_at, updated_at)
VALUES (4, 'PAYMENT', 'EXPIRED', '2023-09-01', '2023-10-01', 1, 29.99, 'EUR', false, NOW(), NOW());

-- Insertar pago completado para la membresía expirada del usuario 4
INSERT INTO membership_payments (membership_id, user_id, amount, currency, status, payment_method, processed_at, transaction_id, created_at, updated_at)
VALUES 
(4, 4, 29.99, 'EUR', 'COMPLETED', 'CREDIT_CARD', '2023-09-01 10:30:00', 'TXN_EXPIRED_001', NOW(), NOW());

-- Asegurar que el usuario 4 sea premium pero con membresía expirada
UPDATE users 
SET is_premium = true, 
    premium_by = 'PAYMENT',
    updated_at = NOW()
WHERE id = 4; 