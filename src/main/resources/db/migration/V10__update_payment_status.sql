-- V10: Update payment status to new English values
-- Update existing payment statuses to new English nomenclature

-- Update COMPLETED to COLLECTED
UPDATE membership_payments 
SET status = 'COLLECTED' 
WHERE status = 'COMPLETED';

-- Update PENDING to PENDING (no change needed)
UPDATE membership_payments 
SET status = 'PENDING' 
WHERE status = 'PENDING';

-- Update FAILED to OBSERVED (payments that failed will be marked for observation)
UPDATE membership_payments 
SET status = 'OBSERVED' 
WHERE status = 'FAILED';

-- Update CANCELLED to CANCELLED (no change needed)
UPDATE membership_payments 
SET status = 'CANCELLED' 
WHERE status = 'CANCELLED';

-- Update REFUNDED to CANCELLED (refunded payments are essentially cancelled)
UPDATE membership_payments 
SET status = 'CANCELLED' 
WHERE status = 'REFUNDED';

-- Update any Spanish states to English equivalents
UPDATE membership_payments 
SET status = 'COLLECTED' 
WHERE status = 'Cobrado';

UPDATE membership_payments 
SET status = 'PENDING' 
WHERE status = 'Pendiente';

UPDATE membership_payments 
SET status = 'OBSERVED' 
WHERE status = 'Observado';

UPDATE membership_payments 
SET status = 'CANCELLED' 
WHERE status = 'Cancelado';

-- Update column definition to support longer varchar for status names
ALTER TABLE membership_payments 
MODIFY COLUMN status VARCHAR(20) NOT NULL;

-- Update payment_method column to support longer varchar values
ALTER TABLE membership_payments 
MODIFY COLUMN payment_method VARCHAR(50) NOT NULL; 