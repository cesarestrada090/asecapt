-- Crear tabla de tipos de servicios
CREATE TABLE service_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insertar los tipos de servicios predefinidos
INSERT INTO service_types (name, description) VALUES 
('Plan Mensual', 'Servicio de entrenamiento con duración de un mes'),
('Plan Trimestral', 'Servicio de entrenamiento con duración de tres meses'),
('Plan Semestral', 'Servicio de entrenamiento con duración de seis meses'),
('Plan Anual', 'Servicio de entrenamiento con duración de un año');

-- Agregar columna service_type_id a la tabla trainer_services
ALTER TABLE trainer_services ADD COLUMN service_type_id INT;

-- Crear foreign key constraint
ALTER TABLE trainer_services 
ADD CONSTRAINT fk_trainer_services_service_type 
FOREIGN KEY (service_type_id) REFERENCES service_types(id);

-- Migrar datos existentes (asignar "Plan Mensual" por defecto)
UPDATE trainer_services 
SET service_type_id = (SELECT id FROM service_types WHERE name = 'Plan Mensual' LIMIT 1)
WHERE service_type_id IS NULL;

-- Hacer la columna NOT NULL después de migrar los datos
ALTER TABLE trainer_services MODIFY service_type_id INT NOT NULL; 