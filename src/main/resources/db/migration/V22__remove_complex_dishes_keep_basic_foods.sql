-- Eliminar platos complejos y mantener solo alimentos básicos individuales
-- V22: Limpiar base de datos para tener solo ingredientes simples

-- Mostrar los alimentos que se van a eliminar (platos complejos)
SELECT 
    id,
    name,
    description,
    'SERÁ ELIMINADO' as status
FROM food_items 
WHERE name IN (
    'Arroz con Pollo',
    'Papa a la Huancaína', 
    'Lomo Saltado',
    'Ceviche de Pescado'
);

-- Eliminar los platos complejos peruanos
DELETE FROM food_items 
WHERE name IN (
    'Arroz con Pollo',
    'Papa a la Huancaína', 
    'Lomo Saltado',
    'Ceviche de Pescado'
);

-- Mostrar los alimentos básicos que permanecen
SELECT 
    fi.id,
    fi.name,
    fc.name as category,
    fi.serving_size_grams as serving_grams,
    fi.calories_per_serving as calories,
    fi.proteins_per_serving as proteins,
    fi.carbohydrates_per_serving as carbs,
    fi.fats_per_serving as fats,
    'MANTENIDO' as status
FROM food_items fi
LEFT JOIN food_categories fc ON fi.category_id = fc.id
WHERE fi.is_active = true
ORDER BY fc.name, fi.name;

-- Verificar el conteo final de alimentos
SELECT 
    COUNT(*) as total_alimentos_restantes,
    'Solo alimentos básicos individuales' as descripcion
FROM food_items 
WHERE is_active = true; 