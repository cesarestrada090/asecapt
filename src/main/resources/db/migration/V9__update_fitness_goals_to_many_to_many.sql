-- Migración para cambiar de relación ManyToOne a ManyToMany para fitness goals

-- 1. Crear tabla intermedia person_fitness_goal_type
CREATE TABLE IF NOT EXISTS person_fitness_goal_type (
    person_id INT NOT NULL,
    fitness_goal_type_id INT NOT NULL,
    PRIMARY KEY (person_id, fitness_goal_type_id),
    FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE,
    FOREIGN KEY (fitness_goal_type_id) REFERENCES fitness_goal_type(id) ON DELETE CASCADE
);

-- 2. Migrar datos existentes de la columna fitness_goal_type_id a la tabla intermedia
INSERT INTO person_fitness_goal_type (person_id, fitness_goal_type_id)
SELECT id, fitness_goal_type_id 
FROM person 
WHERE fitness_goal_type_id IS NOT NULL;

-- 3. Eliminar la columna fitness_goal_type_id de la tabla person
ALTER TABLE person DROP FOREIGN KEY fk_person_fitness_goal_type;
ALTER TABLE person DROP COLUMN fitness_goal_type_id; 