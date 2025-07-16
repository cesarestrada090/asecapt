-- Actualizar imágenes de alimentos
-- V21: Agregar URLs de imágenes a los alimentos que no las tienen

-- Mostrar estado actual de los alimentos sin imagen
SELECT id, name, image_url 
FROM food_items 
WHERE image_url IS NULL OR image_url = '';

-- Actualizar imágenes por ID específico
UPDATE food_items SET 
    image_url = 'https://images.unsplash.com/photo-1516684732162-798a0062be99?w=400&h=300&fit=crop',
    updated_at = NOW()
WHERE id = 5 AND name = 'Huevo entero';

UPDATE food_items SET 
    image_url = 'https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400&h=300&fit=crop',
    updated_at = NOW()
WHERE id = 6 AND name = 'Atún en agua (enlatado)';

UPDATE food_items SET 
    image_url = 'https://images.unsplash.com/photo-1536304993881-ff6e9eefa2a6?w=400&h=300&fit=crop',
    updated_at = NOW()
WHERE id = 7 AND name = 'Arroz blanco cocido';

UPDATE food_items SET 
    image_url = 'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=400&h=300&fit=crop',
    updated_at = NOW()
WHERE id = 8 AND name = 'Arroz integral cocido';

-- Verificar que todas las actualizaciones se aplicaron correctamente
SELECT 
    id,
    name,
    image_url,
    updated_at
FROM food_items 
WHERE id IN (5, 6, 7, 8)
ORDER BY id;

-- Mostrar estado final - todos los alimentos con sus imágenes
SELECT 
    fi.id,
    fi.name,
    fc.name as category,
    CASE 
        WHEN fi.image_url IS NULL OR fi.image_url = '' THEN 'SIN IMAGEN'
        ELSE 'CON IMAGEN'
    END as image_status,
    fi.image_url,
    fi.is_popular
FROM food_items fi
LEFT JOIN food_categories fc ON fi.category_id = fc.id
WHERE fi.is_active = true
ORDER BY fi.id; 