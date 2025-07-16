-- Eliminar campos de duración y sesiones por semana
ALTER TABLE trainer_services
DROP COLUMN duration_months,
DROP COLUMN sessions_per_week,
DROP INDEX idx_duration;

-- Actualizar la tabla de enrollments para usar solo fechas
ALTER TABLE service_enrollments
MODIFY COLUMN start_date DATE NOT NULL,
MODIFY COLUMN end_date DATE NOT NULL; 