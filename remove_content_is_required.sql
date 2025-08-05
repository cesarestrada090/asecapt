-- ========================================================
-- SCRIPT: Remove is_required column from content table
-- REASON: isRequired should only exist in program_content 
--         relationship, not as a property of content itself
-- ========================================================

-- Check current structure of content table
DESCRIBE content;

-- Check if is_required column exists before dropping
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'content' 
  AND COLUMN_NAME = 'is_required';

-- Drop the is_required column from content table
-- (Only execute if the column exists)
ALTER TABLE content DROP COLUMN IF EXISTS is_required;

-- Verify the column has been removed
SELECT 'AFTER REMOVAL:' as status;
DESCRIBE content;

-- Verify program_content still has is_required (this should remain)
SELECT 'PROGRAM_CONTENT TABLE (should still have is_required):' as status;
DESCRIBE program_content; 