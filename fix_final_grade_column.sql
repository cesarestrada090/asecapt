-- Script para corregir el rango de la columna final_grade
-- Cambiar la precisión de DECIMAL(4,2) a DECIMAL(5,2) para permitir notas de 100

ALTER TABLE enrollment 
MODIFY COLUMN final_grade DECIMAL(5,2);

-- Verificar que el cambio se aplicó correctamente
DESCRIBE enrollment;
