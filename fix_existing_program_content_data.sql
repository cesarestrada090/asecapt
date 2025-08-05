-- =======================================================
-- SCRIPT: Fix Existing Program Content Data
-- PURPOSE: Update existing program_content records with:
--          1. Proper order_index values
--          2. Mixed isRequired values (some true, some false)
-- =======================================================

-- First, let's see what we have
SELECT 
    program_id,
    content_id,
    order_index,
    is_required,
    created_at
FROM program_content 
ORDER BY program_id, content_id;

-- =======================================================
-- OPTION 1: Update order_index for all existing records
-- (This assigns sequential order based on content_id)
-- =======================================================

-- Update order_index based on content_id sequence within each program
SET @row_number = 0;
SET @prev_program_id = '';

UPDATE program_content pc1
JOIN (
    SELECT 
        program_id,
        content_id,
        @row_number := CASE 
            WHEN @prev_program_id = program_id THEN @row_number + 1 
            ELSE 1 
        END AS new_order_index,
        @prev_program_id := program_id
    FROM program_content 
    ORDER BY program_id, content_id
) pc2 ON pc1.program_id = pc2.program_id AND pc1.content_id = pc2.content_id
SET pc1.order_index = pc2.new_order_index
WHERE pc1.order_index IS NULL;

-- =======================================================
-- OPTION 2A: Make all existing content OPTIONAL (false)
-- (Uncomment if you want all existing content to be optional)
-- =======================================================

-- UPDATE program_content 
-- SET is_required = false 
-- WHERE is_required = true;

-- =======================================================
-- OPTION 2B: Set mixed required/optional pattern
-- (Makes every 2nd and 3rd content optional, others required)
-- =======================================================

UPDATE program_content 
SET is_required = false 
WHERE (content_id % 3) IN (1, 2);

-- This creates a pattern like:
-- Content 1: Optional (false)
-- Content 2: Optional (false) 
-- Content 3: Required (true)
-- Content 4: Optional (false)
-- Content 5: Optional (false)
-- Content 6: Required (true)
-- etc.

-- =======================================================
-- OPTION 2C: Set specific contents as optional
-- (Customize this based on your needs)
-- =======================================================

-- Example: Make specific content IDs optional
-- UPDATE program_content 
-- SET is_required = false 
-- WHERE content_id IN (1, 3, 5, 7);

-- =======================================================
-- VERIFY RESULTS
-- =======================================================

-- Check the updated data
SELECT 
    program_id,
    content_id,
    order_index,
    is_required,
    CASE WHEN is_required THEN 'Obligatorio' ELSE 'Opcional' END as status_text,
    created_at
FROM program_content 
ORDER BY program_id, order_index;

-- Count by status
SELECT 
    is_required,
    CASE WHEN is_required THEN 'Obligatorio' ELSE 'Opcional' END as status_text,
    COUNT(*) as count
FROM program_content 
GROUP BY is_required; 