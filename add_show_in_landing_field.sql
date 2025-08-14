-- Agregar campo show_in_landing a la tabla program
ALTER TABLE program 
ADD COLUMN show_in_landing BOOLEAN DEFAULT FALSE NOT NULL 
COMMENT 'Indica si el programa debe mostrarse en el landing page';

-- Actualizar algunos programas existentes para que se muestren en el landing page
UPDATE program SET show_in_landing = TRUE WHERE id IN (1, 2, 3, 4, 5);
