-- ===============================================
-- QUICK FIX: Update existing program_content data
-- ===============================================

-- First, let's see what we have
SELECT 'BEFORE UPDATE:' as status;
SELECT program_id, content_id, order_index, is_required FROM program_content ORDER BY program_id, content_id;

-- Fix 1: Add proper order_index values
UPDATE program_content SET order_index = 1 WHERE content_id = 1;
UPDATE program_content SET order_index = 2 WHERE content_id = 2;
UPDATE program_content SET order_index = 3 WHERE content_id = 3;
UPDATE program_content SET order_index = 4 WHERE content_id = 4;
UPDATE program_content SET order_index = 5 WHERE content_id = 5;
UPDATE program_content SET order_index = 6 WHERE content_id = 6;
UPDATE program_content SET order_index = 7 WHERE content_id = 7;
UPDATE program_content SET order_index = 8 WHERE content_id = 8;

-- Fix 2: Make some content optional (false) to test the functionality
UPDATE program_content SET is_required = false WHERE content_id IN (1, 3, 5, 7);

-- Verify results
SELECT 'AFTER UPDATE:' as status;
SELECT 
    program_id, 
    content_id, 
    order_index, 
    is_required,
    CASE WHEN is_required THEN 'Obligatorio' ELSE 'Opcional' END as status_text
FROM program_content 
ORDER BY program_id, order_index; 