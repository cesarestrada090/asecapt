-- V12: Fix existing contract payments status

-- First, let's see what we have and update existing contract payments
-- Update payments for contract memberships that show "COMPLETED" in description to "COLLECTED"

-- Update contract payments that are currently COMPLETED to COLLECTED
UPDATE membership_payments mp
INNER JOIN user_memberships um ON mp.membership_id = um.id
SET mp.status = 'COLLECTED',
    mp.processed_at = COALESCE(mp.processed_at, NOW())
WHERE um.membership_type = 'CONTRACT'
  AND mp.status = 'COMPLETED';

-- Update any payments with Spanish states to English
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

-- Create payments for contract memberships that don't have payment records
INSERT INTO membership_payments (membership_id, user_id, amount, currency, status, payment_method, processed_at, transaction_id, description, created_at)
SELECT 
    um.id as membership_id,
    um.user_id,
    um.payment_amount as amount,
    um.currency,
    'COLLECTED' as status,  -- Mark as collected since contracts show "COMPLETED" in description
    'CONTRACT_PAYMENT' as payment_method,
    um.created_at as processed_at,
    CONCAT('CONTRACT_', um.id, '_', UNIX_TIMESTAMP()) as transaction_id,
    'Contract payment - Service completed' as description,
    NOW() as created_at
FROM user_memberships um
WHERE um.membership_type = 'CONTRACT'
  AND um.id NOT IN (
      SELECT DISTINCT membership_id 
      FROM membership_payments 
      WHERE membership_id IS NOT NULL
  )
  AND um.payment_amount IS NOT NULL;

-- Update payment method for existing contract payments
UPDATE membership_payments mp
INNER JOIN user_memberships um ON mp.membership_id = um.id
SET mp.payment_method = 'CONTRACT_PAYMENT'
WHERE um.membership_type = 'CONTRACT'
  AND mp.payment_method != 'CONTRACT_PAYMENT'; 