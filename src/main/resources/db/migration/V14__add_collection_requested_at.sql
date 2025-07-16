-- Add collection_requested_at column to track when trainer requested payment collection
ALTER TABLE membership_payments 
ADD COLUMN collection_requested_at TIMESTAMP NULL;

-- Add comment for documentation
COMMENT ON COLUMN membership_payments.collection_requested_at IS 'Timestamp when trainer requested payment collection (moved to PROCESSING_COLLECTION status)'; 