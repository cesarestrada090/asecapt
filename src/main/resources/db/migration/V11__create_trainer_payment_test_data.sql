-- V11: Create test data for trainer payments with new status system

-- Insert some contract memberships with trainer payments
INSERT INTO user_memberships (user_id, membership_type, status, start_date, end_date, trainer_id, contract_details, payment_amount, currency, created_at, updated_at)
VALUES 
(1, 'CONTRACT', 'ACTIVE', '2024-01-01', '2024-04-01', 3, 'Personal training contract for 3 months', 150.00, 'EUR', NOW(), NOW()),
(2, 'CONTRACT', 'ACTIVE', '2024-01-15', '2024-04-15', 3, 'Nutrition and training program', 200.00, 'EUR', NOW(), NOW()),
(4, 'CONTRACT', 'ACTIVE', '2024-02-01', '2024-05-01', 3, 'Weight loss program', 180.00, 'EUR', NOW(), NOW());

-- Insert payments for these contracts with different statuses
INSERT INTO membership_payments (membership_id, user_id, amount, currency, status, payment_method, processed_at, transaction_id, description, created_at)
VALUES 
-- COLLECTED payments (trainer can see these)
(5, 1, 150.00, 'EUR', 'COLLECTED', 'CONTRACT_PAYMENT', '2024-01-15 10:00:00', 'CONTRACT_001_2024', 'Contract payment - Service completed', NOW()),
(6, 2, 200.00, 'EUR', 'COLLECTED', 'CONTRACT_PAYMENT', '2024-02-01 14:30:00', 'CONTRACT_002_2024', 'Contract payment - Service completed', NOW()),
(7, 4, 180.00, 'EUR', 'COLLECTED', 'CONTRACT_PAYMENT', '2024-02-15 09:15:00', 'CONTRACT_003_2024', 'Contract payment - Service completed', NOW()),

-- PENDING payments (waiting for service completion)
(5, 1, 150.00, 'EUR', 'PENDING', 'CONTRACT_PAYMENT', NULL, 'CONTRACT_004_2024', 'Contract payment - Pending service completion', NOW()),
(6, 2, 200.00, 'EUR', 'PENDING', 'CONTRACT_PAYMENT', NULL, 'CONTRACT_005_2024', 'Contract payment - Pending service completion', NOW()),

-- OBSERVED payment (needs review)
(7, 4, 180.00, 'EUR', 'OBSERVED', 'CONTRACT_PAYMENT', '2024-02-20 16:45:00', 'CONTRACT_006_2024', 'Contract payment - Under review', NOW()),

-- CANCELLED payment
(5, 1, 150.00, 'EUR', 'CANCELLED', 'CONTRACT_PAYMENT', '2024-01-10 11:20:00', 'CONTRACT_007_2024', 'Contract payment - Cancelled by client', NOW());

-- Update the membership IDs to match the actual inserted IDs
-- Note: These IDs might need adjustment based on existing data 