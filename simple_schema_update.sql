-- Simple Schema Update - Only add missing columns
-- Compatible with existing composite keys

USE asecapt;

-- ===================================
-- UPDATE PROGRAM TABLE
-- ===================================

-- Add missing columns to program table
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program' AND column_name = 'title') = 0,
    'ALTER TABLE program ADD COLUMN title VARCHAR(255)',
    'SELECT "Column title already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program' AND column_name = 'type') = 0,
    'ALTER TABLE program ADD COLUMN type VARCHAR(50)',
    'SELECT "Column type already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program' AND column_name = 'category') = 0,
    'ALTER TABLE program ADD COLUMN category VARCHAR(100)',
    'SELECT "Column category already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program' AND column_name = 'price') = 0,
    'ALTER TABLE program ADD COLUMN price VARCHAR(50)',
    'SELECT "Column price already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program' AND column_name = 'start_date') = 0,
    'ALTER TABLE program ADD COLUMN start_date VARCHAR(50)',
    'SELECT "Column start_date already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program' AND column_name = 'end_date') = 0,
    'ALTER TABLE program ADD COLUMN end_date VARCHAR(50)',
    'SELECT "Column end_date already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program' AND column_name = 'instructor') = 0,
    'ALTER TABLE program ADD COLUMN instructor VARCHAR(100)',
    'SELECT "Column instructor already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program' AND column_name = 'max_students') = 0,
    'ALTER TABLE program ADD COLUMN max_students INT',
    'SELECT "Column max_students already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program' AND column_name = 'prerequisites') = 0,
    'ALTER TABLE program ADD COLUMN prerequisites TEXT',
    'SELECT "Column prerequisites already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program' AND column_name = 'objectives') = 0,
    'ALTER TABLE program ADD COLUMN objectives TEXT',
    'SELECT "Column objectives already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Copy existing name to title
UPDATE program SET title = name WHERE title IS NULL OR title = '';

-- ===================================
-- UPDATE CONTENT TABLE
-- ===================================

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'content' AND column_name = 'title') = 0,
    'ALTER TABLE content ADD COLUMN title VARCHAR(255)',
    'SELECT "Column title already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'content' AND column_name = 'type') = 0,
    'ALTER TABLE content ADD COLUMN type VARCHAR(50)',
    'SELECT "Column type already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'content' AND column_name = 'duration') = 0,
    'ALTER TABLE content ADD COLUMN duration VARCHAR(50)',
    'SELECT "Column duration already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'content' AND column_name = 'content') = 0,
    'ALTER TABLE content ADD COLUMN content LONGTEXT',
    'SELECT "Column content already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'content' AND column_name = 'is_required') = 0,
    'ALTER TABLE content ADD COLUMN is_required BOOLEAN DEFAULT TRUE',
    'SELECT "Column is_required already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'content' AND column_name = 'order_index') = 0,
    'ALTER TABLE content ADD COLUMN order_index INT',
    'SELECT "Column order_index already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Copy existing topic to title
UPDATE content SET title = topic WHERE title IS NULL OR title = '';

-- ===================================
-- UPDATE PROGRAM_CONTENT TABLE (KEEP COMPOSITE KEY)
-- ===================================

-- Only add new columns, keep existing composite primary key (program_id, content_id)
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program_content' AND column_name = 'order_index') = 0,
    'ALTER TABLE program_content ADD COLUMN order_index INT',
    'SELECT "Column order_index already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program_content' AND column_name = 'is_required') = 0,
    'ALTER TABLE program_content ADD COLUMN is_required BOOLEAN DEFAULT TRUE',
    'SELECT "Column is_required already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = 'asecapt' AND table_name = 'program_content' AND column_name = 'created_at') = 0,
    'ALTER TABLE program_content ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP',
    'SELECT "Column created_at already exists"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ===================================
-- SAMPLE DATA
-- ===================================

-- Sample Programs
INSERT IGNORE INTO program (title, description, type, category, status, duration, credits, price, instructor, objectives)
VALUES 
('Fundamentos de Programación', 'Curso básico de programación con Python y JavaScript', 'course', 'Programación', 'active', '40 horas', 3, 'S/. 299', 'Prof. García', 'Aprender los fundamentos de la programación orientada a objetos'),
('Especialización en Data Science', 'Programa especializado en ciencia de datos y machine learning', 'specialization', 'Data Science', 'active', '120 horas', 8, 'S/. 899', 'Prof. López', 'Dominar las técnicas de análisis de datos y machine learning'),
('Certificación en Ciberseguridad', 'Programa de certificación profesional en seguridad informática', 'certification', 'Seguridad', 'active', '80 horas', 5, 'S/. 599', 'Prof. Martín', 'Obtener certificación en seguridad informática'),
('Diseño Web Avanzado', 'Curso avanzado de diseño y desarrollo web con tecnologías modernas', 'course', 'Diseño', 'inactive', '60 horas', 4, 'S/. 449', 'Prof. Rivera', 'Crear sitios web modernos y responsivos');

-- Sample Content/Modules
INSERT IGNORE INTO content (title, description, type, duration, content, is_required)
VALUES 
('Introducción a JavaScript', 'Fundamentos del lenguaje JavaScript', 'module', '8 horas', 'Variables, funciones, objetos y DOM manipulation', TRUE),
('Python para Principiantes', 'Conceptos básicos de Python', 'module', '10 horas', 'Sintaxis, estructuras de datos y programación orientada a objetos', TRUE),
('HTML y CSS Básico', 'Fundamentos del desarrollo web', 'lesson', '6 horas', 'Estructura HTML semántica y estilos CSS', TRUE),
('Proyecto Final', 'Desarrollo de aplicación web completa', 'assignment', '15 horas', 'Crear una aplicación web usando todas las tecnologías aprendidas', TRUE),
('Examen de Certificación', 'Evaluación final del curso', 'exam', '2 horas', 'Examen teórico y práctico de los conceptos aprendidos', TRUE);

-- Sample Program-Content Relationships (using composite key)
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
-- VERIFICATION
-- ===================================

SELECT 'Schema update completed! Composite keys preserved.' as Status;
DESCRIBE program_content; 