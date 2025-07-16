-- Migration to add bio field to person table
ALTER TABLE person 
    ADD COLUMN bio VARCHAR(1200); 