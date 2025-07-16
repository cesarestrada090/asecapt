-- V10: Agregar CONTRACT_PAYMENT al ENUM de payment_method

-- Actualizar el ENUM de payment_method para incluir CONTRACT_PAYMENT
ALTER TABLE membership_payments 
MODIFY COLUMN payment_method ENUM(
    'CREDIT_CARD', 
    'DEBIT_CARD', 
    'PAYPAL', 
    'BANK_TRANSFER', 
    'STRIPE', 
    'CONTRACT_PAYMENT',
    'OTHER'
) NOT NULL; 