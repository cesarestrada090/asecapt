-- Add soft delete columns to enrollment table
ALTER TABLE enrollment 
ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE enrollment 
ADD COLUMN deleted_at DATETIME NULL;

-- Add index for performance on deleted field
CREATE INDEX idx_enrollment_deleted ON enrollment(deleted);
CREATE INDEX idx_enrollment_user_program_deleted ON enrollment(user_id, program_id, deleted);

-- Update any existing records to have deleted = false (just in case)
UPDATE enrollment SET deleted = FALSE WHERE deleted IS NULL;