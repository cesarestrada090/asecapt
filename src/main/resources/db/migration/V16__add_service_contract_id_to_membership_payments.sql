-- Add service_contract_id column to membership_payments table
-- This links payments that originate from service contracts

ALTER TABLE membership_payments 
ADD COLUMN service_contract_id INT NULL AFTER membership_id,
ADD INDEX idx_membership_payments_service_contract (service_contract_id);

-- Add foreign key constraint
ALTER TABLE membership_payments 
ADD CONSTRAINT fk_membership_payments_service_contract 
FOREIGN KEY (service_contract_id) REFERENCES service_contracts(id) 
ON DELETE SET NULL ON UPDATE CASCADE;

-- Add comment to explain the column
ALTER TABLE membership_payments 
MODIFY COLUMN service_contract_id INT NULL 
COMMENT 'ID of the service contract that generated this payment (NULL for subscription payments)';

-- Update existing contract payments to link them with their service contracts
-- This query links payments with contracts based on user_id, amount, and timing
UPDATE membership_payments mp
INNER JOIN service_contracts sc ON sc.client_id = mp.user_id
SET mp.service_contract_id = sc.id
WHERE mp.service_contract_id IS NULL
  AND mp.description LIKE '%contrato%'
  AND ABS(mp.amount - sc.total_amount) < 0.01
  AND mp.created_at >= sc.created_at
  AND mp.created_at <= DATE_ADD(sc.created_at, INTERVAL 1 DAY);

-- Add a composite index for efficient queries
CREATE INDEX idx_membership_payments_user_contract ON membership_payments(user_id, service_contract_id);
CREATE INDEX idx_membership_payments_contract_status ON membership_payments(service_contract_id, status); 