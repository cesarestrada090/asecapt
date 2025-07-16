-- Migración para cambiar duration_months de ENUM a INT
-- Permitir valores flexibles de 1-24 meses

-- Cambiar la columna duration_months de ENUM a INT
ALTER TABLE trainer_services 
MODIFY COLUMN duration_months INT NOT NULL 
CHECK (duration_months >= 1 AND duration_months <= 24); 