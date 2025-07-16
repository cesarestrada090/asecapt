-- Eliminar la columna name de trainer_services ya que ahora se obtiene através de service_type
ALTER TABLE trainer_services DROP COLUMN name;

-- Agregar constraint de unicidad para evitar servicios duplicados del mismo tipo para el mismo trainer
ALTER TABLE trainer_services 
ADD CONSTRAINT uk_trainer_service_type 
UNIQUE (trainer_id, service_type_id); 