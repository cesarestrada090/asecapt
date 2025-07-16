-- Add description and icon columns to fitness_goal_type table
ALTER TABLE fitness_goal_type 
ADD COLUMN description TEXT,
ADD COLUMN icon VARCHAR(255) NOT NULL DEFAULT 'flag';

-- Update existing records with their corresponding descriptions and icons
UPDATE fitness_goal_type 
SET description = 'Enfocado en perder peso y definir tu figura corporal',
    icon = 'trending_down'
WHERE name = 'Reducción de grasa corporal';

UPDATE fitness_goal_type 
SET description = 'Orientado a ganar músculo y aumentar tu fuerza',
    icon = 'fitness_center'
WHERE name = 'Aumento de masa muscular';

UPDATE fitness_goal_type 
SET description = 'Centrado en mejorar tu bienestar general y calidad de vida',
    icon = 'favorite'
WHERE name = 'Salud';

UPDATE fitness_goal_type 
SET description = 'Dirigido a establecer rutinas saludables y consistentes',
    icon = 'schedule'
WHERE name = 'Crear hábitos';

UPDATE fitness_goal_type 
SET description = 'Objetivos personalizados según tus necesidades específicas',
    icon = 'more_horiz'
WHERE name = 'Otros';

-- Set default description for any records that don't match the above
UPDATE fitness_goal_type 
SET description = 'Tu objetivo fitness personalizado'
WHERE description IS NULL; 