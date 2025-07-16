-- V9: Agregar registros de pago para contratos existentes que no los tienen

-- Insertar pagos para membresías de contrato existentes que no tienen registros de pago
INSERT INTO membership_payments (membership_id, user_id, amount, currency, status, payment_method, processed_at, transaction_id, description, created_at, updated_at)
SELECT 
    um.id as membership_id,
    um.user_id,
    COALESCE(um.payment_amount, 100.00) as amount, -- Usar payment_amount si existe, sino valor por defecto
    COALESCE(um.currency, 'EUR') as currency,
    'COMPLETED' as status,
    'OTHER' as payment_method, -- Usar OTHER temporalmente hasta que se agregue CONTRACT_PAYMENT
    um.start_date as processed_at, -- Usar la fecha de inicio del contrato
    CONCAT('CONTRACT_', um.id, '_', UNIX_TIMESTAMP()) as transaction_id,
    'Pago de contrato con entrenador (migración)' as description,
    NOW() as created_at,
    NOW() as updated_at
FROM user_memberships um
WHERE um.membership_type = 'CONTRACT'
  AND um.id NOT IN (
      SELECT DISTINCT membership_id 
      FROM membership_payments 
      WHERE membership_id IS NOT NULL
  );

-- Actualizar las membresías de contrato para incluir información de pago si no la tienen
UPDATE user_memberships 
SET payment_amount = CASE 
    WHEN payment_amount IS NULL THEN 100.00 
    ELSE payment_amount 
END,
currency = CASE 
    WHEN currency IS NULL THEN 'EUR' 
    ELSE currency 
END,
updated_at = NOW()
WHERE membership_type = 'CONTRACT' 
  AND (payment_amount IS NULL OR currency IS NULL); 