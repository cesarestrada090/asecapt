-- V10: Cambiar payment_method de ENUM a VARCHAR para mayor flexibilidad

-- Cambiar la columna payment_method de ENUM a VARCHAR
ALTER TABLE membership_payments 
MODIFY COLUMN payment_method VARCHAR(50) NOT NULL; 