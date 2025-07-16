-- Add completion_date column to service_contracts table
-- This records when the client actually marked the contract as completed

ALTER TABLE service_contracts 
ADD COLUMN completion_date TIMESTAMP NULL AFTER end_date;

-- Add comment to explain the column
ALTER TABLE service_contracts 
MODIFY COLUMN completion_date TIMESTAMP NULL 
COMMENT 'Actual date when client marked the contract as completed (different from end_date which is planned end date)';

-- Add index for efficient queries on completion_date
CREATE INDEX idx_service_contracts_completion_date ON service_contracts(completion_date);

-- Add composite index for status and completion queries
CREATE INDEX idx_service_contracts_status_completion ON service_contracts(contract_status, completion_date); 