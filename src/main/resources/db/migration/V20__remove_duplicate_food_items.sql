-- Eliminar alimentos duplicados por nombre
-- V20: Limpiar registros duplicados manteniendo el más reciente

-- Primero, mostrar los duplicados que se van a eliminar
SELECT 
    name,
    COUNT(*) as duplicates,
    GROUP_CONCAT(id ORDER BY id) as all_ids,
    MIN(id) as min_id,
    MAX(id) as max_id
FROM food_items 
GROUP BY name 
HAVING COUNT(*) > 1
ORDER BY name;

-- Eliminar duplicados manteniendo solo el registro con ID más alto
DELETE f1 FROM food_items f1
INNER JOIN food_items f2 
WHERE f1.name = f2.name 
AND f1.id < f2.id;

-- Verificar que no quedan duplicados
SELECT 
    name,
    COUNT(*) as count
FROM food_items 
GROUP BY name 
HAVING COUNT(*) > 1;

-- Mostrar el estado final de todos los alimentos
SELECT 
    fi.id,
    fi.name,
    fc.name as category,
    fi.serving_size_grams as serving_grams,
    fi.calories_per_serving as calories,
    fi.proteins_per_serving as proteins,
    fi.carbohydrates_per_serving as carbs,
    fi.fats_per_serving as fats,
    fi.is_popular,
    fi.created_at
FROM food_items fi
LEFT JOIN food_categories fc ON fi.category_id = fc.id
WHERE fi.is_active = true
ORDER BY fc.name, fi.name; 