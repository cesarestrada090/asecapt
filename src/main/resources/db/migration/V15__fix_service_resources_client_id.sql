-- Fix existing service_resources to have correct client_id
-- Update service_resources that have a service_id but no client_id
UPDATE service_resources sr
INNER JOIN trainer_services ts ON sr.service_id = ts.id
INNER JOIN service_contracts sc ON sc.service_id = ts.id
SET sr.client_id = sc.client_id
WHERE sr.service_id IS NOT NULL 
  AND sr.client_id IS NULL
  AND sc.contract_status = 'ACTIVE';

-- Also update any resources that might have incorrect client_id
-- by matching with the active contract for that service
UPDATE service_resources sr
INNER JOIN trainer_services ts ON sr.service_id = ts.id
INNER JOIN service_contracts sc ON sc.service_id = ts.id
SET sr.client_id = sc.client_id
WHERE sr.service_id IS NOT NULL 
  AND sc.contract_status = 'ACTIVE'
  AND sr.client_id != sc.client_id; 