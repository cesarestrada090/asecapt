-- Script to fix program_content table primary key issue
USE asecapt;

-- First, let's see the current structure
DESCRIBE program_content;

-- Drop the existing primary key constraint
ALTER TABLE program_content DROP PRIMARY KEY;

-- Now add the new id column as primary key
ALTER TABLE program_content ADD COLUMN id INT AUTO_INCREMENT PRIMARY KEY FIRST;

-- Add other missing columns if they don't exist
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program_content' AND column_name = 'order_index') = 0,
    'ALTER TABLE program_content ADD COLUMN order_index INT',
    'SELECT "Column order_index already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program_content' AND column_name = 'is_required') = 0,
    'ALTER TABLE program_content ADD COLUMN is_required BOOLEAN DEFAULT TRUE',
    'SELECT "Column is_required already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program_content' AND column_name = 'created_at') = 0,
    'ALTER TABLE program_content ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP',
    'SELECT "Column created_at already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Show the final structure
SELECT 'Final program_content structure:' as Info;
DESCRIBE program_content; 