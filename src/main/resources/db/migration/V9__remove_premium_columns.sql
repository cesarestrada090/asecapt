-- Remove premium-related columns from user table
-- This migration removes all premium functionality to simplify the system

-- Drop premium-related columns
ALTER TABLE user 
DROP COLUMN IF EXISTS is_premium,
DROP COLUMN IF EXISTS premium_by;

-- Update existing users' data in the SQL script to remove premium references
UPDATE user SET
    updated_at = NOW()
WHERE is_premium IS NOT NULL OR premium_by IS NOT NULL;