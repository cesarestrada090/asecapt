-- Agregar la columna fitness_goal_type_id a la tabla person
ALTER TABLE person 
ADD COLUMN fitness_goal_type_id INT,
ADD CONSTRAINT fk_person_fitness_goal_type 
    FOREIGN KEY (fitness_goal_type_id) 
    REFERENCES fitness_goal_type(id);

-- Insertar los tipos de objetivos fitness predefinidos si no existen
INSERT IGNORE INTO fitness_goal_type (name) VALUES 
('Reducción de grasa corporal'),
('Aumento de masa muscular'),
('Salud'),
('Crear hábitos'),
('Otros'); 