-- Insertar datos de alimentos para la calculadora de macros
-- V19: Agregar huevos, atún y arroz

-- Primero verificar que las categorías base existan, si no, crearlas
INSERT IGNORE INTO food_categories (name, description, icon, is_active, created_at, updated_at) VALUES
('Proteínas', 'Alimentos ricos en proteínas', '🥩', true, NOW(), NOW()),
('Carbohidratos', 'Alimentos ricos en carbohidratos', '🌾', true, NOW(), NOW()),
('Pescados y Mariscos', 'Pescados, mariscos y productos del mar', '🐟', true, NOW(), NOW());

-- Insertar los alimentos específicos solicitados
INSERT INTO food_items (
    name, 
    description, 
    category_id, 
    image_url, 
    serving_size_grams,
    calories_per_serving,
    proteins_per_serving,
    carbohydrates_per_serving,
    fats_per_serving,
    fiber_per_serving,
    sugar_per_serving,
    sodium_per_serving,
    is_popular,
    is_active,
    created_by,
    created_at,
    updated_at
) VALUES
-- Huevos (1 huevo grande = ~50g)
(
    'Huevo entero',
    'Huevo de gallina entero, excelente fuente de proteína completa (1 huevo grande)',
    (SELECT id FROM food_categories WHERE name = 'Proteínas' LIMIT 1),
    null,
    50,
    70.0,
    6.3,
    0.4,
    4.8,
    0.0,
    0.2,
    70.0,
    true,
    true,
    1,
    NOW(),
    NOW()
),

-- Atún en agua (por 100g)
(
    'Atún en agua (enlatado)',
    'Atún enlatado en agua, bajo en grasa y alto en proteína',
    (SELECT id FROM food_categories WHERE name = 'Pescados y Mariscos' LIMIT 1),
    null,
    100,
    116.0,
    25.5,
    0.0,
    1.0,
    0.0,
    0.0,
    320.0,
    true,
    true,
    1,
    NOW(),
    NOW()
),

-- Arroz blanco cocido (por 100g)
(
    'Arroz blanco cocido',
    'Arroz blanco hervido, fuente principal de carbohidratos',
    (SELECT id FROM food_categories WHERE name = 'Carbohidratos' LIMIT 1),
    null,
    100,
    130.0,
    2.7,
    28.0,
    0.3,
    0.4,
    0.1,
    1.0,
    true,
    true,
    1,
    NOW(),
    NOW()
),

-- Arroz integral cocido (por 100g) - alternativa más nutritiva
(
    'Arroz integral cocido',
    'Arroz integral hervido, más fibra y nutrientes que el arroz blanco',
    (SELECT id FROM food_categories WHERE name = 'Carbohidratos' LIMIT 1),
    null,
    100,
    123.0,
    2.6,
    25.0,
    1.0,
    1.8,
    0.2,
    4.0,
    true,
    true,
    1,
    NOW(),
    NOW()
);

-- Verificar que los datos se insertaron correctamente
SELECT 
    fi.name,
    fc.name as category,
    fi.serving_size_grams as serving_grams,
    fi.calories_per_serving as calories,
    fi.proteins_per_serving as proteins,
    fi.carbohydrates_per_serving as carbs,
    fi.fats_per_serving as fats,
    fi.fiber_per_serving as fiber
FROM food_items fi
JOIN food_categories fc ON fi.category_id = fc.id
WHERE fi.name IN ('Huevo entero', 'Atún en agua (enlatado)', 'Arroz blanco cocido', 'Arroz integral cocido')
ORDER BY fi.name; 