-- SQL Script to Update Database Schema for Course Maintainer
-- Execute this script to add the new columns needed for the course maintainer

USE asecapt;

-- ===================================
-- UPDATE PROGRAM TABLE
-- ===================================

-- Add new columns to program table
ALTER TABLE program 
ADD COLUMN IF NOT EXISTS title VARCHAR(255),
ADD COLUMN IF NOT EXISTS type VARCHAR(50),
ADD COLUMN IF NOT EXISTS category VARCHAR(100),
ADD COLUMN IF NOT EXISTS price VARCHAR(50),
ADD COLUMN IF NOT EXISTS start_date VARCHAR(50),
ADD COLUMN IF NOT EXISTS end_date VARCHAR(50),
ADD COLUMN IF NOT EXISTS instructor VARCHAR(100),
ADD COLUMN IF NOT EXISTS max_students INT,
ADD COLUMN IF NOT EXISTS prerequisites TEXT,
ADD COLUMN IF NOT EXISTS objectives TEXT;

-- Copy existing name to title for backwards compatibility
UPDATE program SET title = name WHERE title IS NULL;

-- Update status default from 'active' to 'draft'
ALTER TABLE program ALTER COLUMN status SET DEFAULT 'draft';

-- Make credits nullable
ALTER TABLE program MODIFY COLUMN credits INT NULL;

-- ===================================
-- UPDATE CONTENT TABLE
-- ===================================

-- Add new columns to content table
ALTER TABLE content
ADD COLUMN IF NOT EXISTS title VARCHAR(255),
ADD COLUMN IF NOT EXISTS type VARCHAR(50),
ADD COLUMN IF NOT EXISTS duration VARCHAR(50),
ADD COLUMN IF NOT EXISTS content LONGTEXT,
ADD COLUMN IF NOT EXISTS is_required BOOLEAN DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS order_index INT;

-- Copy existing topic to title for backwards compatibility
UPDATE content SET title = topic WHERE title IS NULL;

-- ===================================
-- UPDATE PROGRAM_CONTENT TABLE
-- ===================================

-- Drop the composite primary key constraint if it exists
-- ALTER TABLE program_content DROP PRIMARY KEY;

-- Add new id column as primary key
ALTER TABLE program_content 
ADD COLUMN IF NOT EXISTS id INT AUTO_INCREMENT PRIMARY KEY FIRST,
ADD COLUMN IF NOT EXISTS order_index INT,
ADD COLUMN IF NOT EXISTS is_required BOOLEAN DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Make program_id and content_id not primary keys but keep them as foreign keys
ALTER TABLE program_content MODIFY COLUMN program_id INT NOT NULL;
ALTER TABLE program_content MODIFY COLUMN content_id INT NOT NULL;

-- ===================================
-- CREATE INDEXES FOR PERFORMANCE
-- ===================================

-- Indexes for program table
CREATE INDEX IF NOT EXISTS idx_program_type ON program(type);
CREATE INDEX IF NOT EXISTS idx_program_status ON program(status);
CREATE INDEX IF NOT EXISTS idx_program_category ON program(category);
CREATE INDEX IF NOT EXISTS idx_program_instructor ON program(instructor);

-- Indexes for content table  
CREATE INDEX IF NOT EXISTS idx_content_type ON content(type);
CREATE INDEX IF NOT EXISTS idx_content_title ON content(title);

-- Indexes for program_content table
CREATE INDEX IF NOT EXISTS idx_program_content_program_id ON program_content(program_id);
CREATE INDEX IF NOT EXISTS idx_program_content_content_id ON program_content(content_id);
CREATE INDEX IF NOT EXISTS idx_program_content_order ON program_content(program_id, order_index);

-- ===================================
-- INSERT SAMPLE DATA FOR TESTING
-- ===================================

-- Sample Programs
INSERT IGNORE INTO program (title, description, type, category, status, duration, credits, price, instructor, objectives)
VALUES 
('Fundamentos de Programación', 'Curso básico de programación con Python y JavaScript', 'course', 'Programación', 'active', '40 horas', 3, 'S/. 299', 'Prof. García', 'Aprender los fundamentos de la programación orientada a objetos'),
('Especialización en Data Science', 'Programa especializado en ciencia de datos y machine learning', 'specialization', 'Data Science', 'active', '120 horas', 8, 'S/. 899', 'Prof. López', 'Dominar las técnicas de análisis de datos y machine learning'),
('Certificación en Ciberseguridad', 'Programa de certificación profesional en seguridad informática', 'certification', 'Seguridad', 'active', '80 horas', 5, 'S/. 599', 'Prof. Martín', 'Obtener certificación en seguridad informática'),
('Diseño Web Avanzado', 'Curso avanzado de diseño y desarrollo web con tecnologías modernas', 'course', 'Diseño', 'draft', '60 horas', 4, 'S/. 449', 'Prof. Rivera', 'Crear sitios web modernos y responsivos');

-- Sample Content/Modules
INSERT IGNORE INTO content (title, description, type, duration, content, is_required)
VALUES 
('Introducción a JavaScript', 'Fundamentos del lenguaje JavaScript', 'module', '8 horas', 'Variables, funciones, objetos y DOM manipulation', TRUE),
('Python para Principiantes', 'Conceptos básicos de Python', 'module', '10 horas', 'Sintaxis, estructuras de datos y programación orientada a objetos', TRUE),
('HTML y CSS Básico', 'Fundamentos del desarrollo web', 'lesson', '6 horas', 'Estructura HTML semántica y estilos CSS', TRUE),
('Proyecto Final', 'Desarrollo de aplicación web completa', 'assignment', '15 horas', 'Crear una aplicación web usando todas las tecnologías aprendidas', TRUE),
('Examen de Certificación', 'Evaluación final del curso', 'exam', '2 horas', 'Examen teórico y práctico de los conceptos aprendidos', TRUE);

-- Sample Program-Content Relationships
INSERT IGNORE INTO program_content (program_id, content_id, order_index, is_required)
SELECT p.id, c.id, 1, TRUE 
FROM program p, content c 
WHERE p.title = 'Fundamentos de Programación' AND c.title = 'Introducción a JavaScript'
LIMIT 1;

INSERT IGNORE INTO program_content (program_id, content_id, order_index, is_required)
SELECT p.id, c.id, 2, TRUE 
FROM program p, content c 
WHERE p.title = 'Fundamentos de Programación' AND c.title = 'Python para Principiantes'
LIMIT 1;

INSERT IGNORE INTO program_content (program_id, content_id, order_index, is_required)
SELECT p.id, c.id, 3, TRUE 
FROM program p, content c 
WHERE p.title = 'Fundamentos de Programación' AND c.title = 'HTML y CSS Básico'
LIMIT 1;

-- ===================================
-- VERIFICATION QUERIES
-- ===================================

-- Verify the updates
SELECT 'Program Table Structure:' as Info;
DESCRIBE program;

SELECT 'Content Table Structure:' as Info;
DESCRIBE content;

SELECT 'Program_Content Table Structure:' as Info;
DESCRIBE program_content;

SELECT 'Sample Programs:' as Info;
SELECT id, title, type, category, status, duration, credits, price FROM program LIMIT 5;

SELECT 'Sample Contents:' as Info;
SELECT id, title, type, duration, is_required FROM content LIMIT 5;

SELECT 'Sample Program-Content Relations:' as Info;
SELECT pc.id, pc.program_id, pc.content_id, pc.order_index, pc.is_required,
       p.title as program_title, c.title as content_title
FROM program_content pc
JOIN program p ON pc.program_id = p.id
JOIN content c ON pc.content_id = c.id
LIMIT 10;

COMMIT; 