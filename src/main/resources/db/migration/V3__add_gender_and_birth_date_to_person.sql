-- Migration to add gender and birth_date fields to person table
ALTER TABLE person 
    ADD COLUMN gender VARCHAR(1),
    ADD COLUMN birth_date DATE; 