-- Migration to add presentation video field to person table
ALTER TABLE person 
    ADD COLUMN presentation_video_id INT; 