-- V13: Add collection status for trainer payments

-- Add new columns for collection status
ALTER TABLE membership_payments 
ADD COLUMN collection_status VARCHAR(30) NULL AFTER status,
ADD COLUMN collected_at TIMESTAMP NULL AFTER processed_at;

-- Update existing COMPLETED payments to be PENDING_CLIENT_APPROVAL by default
-- (client needs to approve that service was completed correctly)
UPDATE membership_payments mp
INNER JOIN user_memberships um ON mp.membership_id = um.id
SET mp.collection_status = 'PENDING_CLIENT_APPROVAL'
WHERE um.membership_type = 'CONTRACT' 
  AND mp.status = 'COMPLETED'
  AND mp.collection_status IS NULL;

-- For demonstration purposes, let's simulate some client approvals
-- Mark some payments as AVAILABLE_FOR_COLLECTION (client approved)
UPDATE membership_payments mp
INNER JOIN user_memberships um ON mp.membership_id = um.id
SET mp.collection_status = 'AVAILABLE_FOR_COLLECTION'
WHERE um.membership_type = 'CONTRACT' 
  AND mp.status = 'COMPLETED'
  AND mp.id % 3 = 0  -- Every third payment is approved by client
  AND mp.collection_status = 'PENDING_CLIENT_APPROVAL';

-- Mark some as already collected by trainer
UPDATE membership_payments mp
INNER JOIN user_memberships um ON mp.membership_id = um.id
SET mp.collection_status = 'COLLECTED',
    mp.collected_at = mp.processed_at
WHERE um.membership_type = 'CONTRACT' 
  AND mp.status = 'COMPLETED'
  AND mp.id % 5 = 0  -- Every fifth payment is already collected
  AND mp.collection_status = 'AVAILABLE_FOR_COLLECTION';

-- Add index for better query performance
CREATE INDEX idx_membership_payments_collection_status ON membership_payments(collection_status);
CREATE INDEX idx_membership_payments_collected_at ON membership_payments(collected_at); 