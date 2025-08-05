-- Simple Schema Update - Only add missing columns
-- Compatible with existing composite keys
-- FIXED: Includes original column values

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

-- Copy existing name to title for backwards compatibility
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

-- Copy existing topic to title for backwards compatibility
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
-- SAMPLE DATA (INCLUDES ORIGINAL COLUMNS)
-- ===================================

-- Sample Programs (including both original and new columns)
INSERT IGNORE INTO program (
    name, description, status, duration, hours, credits,
    title, type, category, price, start_date, end_date, instructor, max_students, prerequisites, objectives
)
VALUES 
(
    'Fundamentos de Programación', 'Curso básico de programación con Python y JavaScript', 'active', '40 horas', 40, 3,
    'Fundamentos de Programación', 'course', 'Programación', 'S/. 299', '2024-02-01', '2024-03-15', 'Prof. García', 25, 'Conocimientos básicos de informática', 'Aprender los fundamentos de la programación orientada a objetos'
),
(
    'Especialización en Data Science', 'Programa especializado en ciencia de datos y machine learning', 'active', '120 horas', 120, 8,
    'Especialización en Data Science', 'specialization', 'Data Science', 'S/. 899', '2024-03-01', '2024-06-30', 'Prof. López', 15, 'Python básico, estadística', 'Dominar las técnicas de análisis de datos y machine learning'
),
(
    'Certificación en Ciberseguridad', 'Programa de certificación profesional en seguridad informática', 'active', '80 horas', 80, 5,
    'Certificación en Ciberseguridad', 'certification', 'Seguridad', 'S/. 599', '2024-04-01', '2024-05-31', 'Prof. Martín', 20, 'Redes básicas, sistemas operativos', 'Obtener certificación en seguridad informática'
),
(
    'Diseño Web Avanzado', 'Curso avanzado de diseño y desarrollo web con tecnologías modernas', 'inactive', '60 horas', 60, 4,
    'Diseño Web Avanzado', 'course', 'Diseño', 'S/. 449', '2024-05-01', '2024-06-15', 'Prof. Rivera', 30, 'HTML, CSS básico', 'Crear sitios web modernos y responsivos'
);

-- Sample Content/Modules (including both original and new columns)
INSERT IGNORE INTO content (
    topic, topic_number, description,
    title, type, duration, content, is_required
)
VALUES 
(
    'Introducción a JavaScript', 1, 'Fundamentos del lenguaje JavaScript',
    'Introducción a JavaScript', 'module', '8 horas', 'Variables, funciones, objetos y DOM manipulation', TRUE
),
(
    'Python para Principiantes', 2, 'Conceptos básicos de Python',
    'Python para Principiantes', 'module', '10 horas', 'Sintaxis, estructuras de datos y programación orientada a objetos', TRUE
),
(
    'HTML y CSS Básico', 3, 'Fundamentos del desarrollo web',
    'HTML y CSS Básico', 'lesson', '6 horas', 'Estructura HTML semántica y estilos CSS', TRUE
),
(
    'Proyecto Final', 4, 'Desarrollo de aplicación web completa',
    'Proyecto Final', 'assignment', '15 horas', 'Crear una aplicación web usando todas las tecnologías aprendidas', TRUE
),
(
    'Examen de Certificación', 5, 'Evaluación final del curso',
    'Examen de Certificación', 'exam', '2 horas', 'Examen teórico y práctico de los conceptos aprendidos', TRUE
);

-- Sample Program-Content Relationships (using composite key)
INSERT IGNORE INTO program_content (program_id, content_id, order_index, is_required)
SELECT p.id, c.id, 1, TRUE 
FROM program p, content c 
WHERE p.name = 'Fundamentos de Programación' AND c.topic = 'Introducción a JavaScript'
LIMIT 1;

INSERT IGNORE INTO program_content (program_id, content_id, order_index, is_required)
SELECT p.id, c.id, 2, TRUE 
FROM program p, content c 
WHERE p.name = 'Fundamentos de Programación' AND c.topic = 'Python para Principiantes'
LIMIT 1;

INSERT IGNORE INTO program_content (program_id, content_id, order_index, is_required)
SELECT p.id, c.id, 3, TRUE 
FROM program p, content c 
WHERE p.name = 'Fundamentos de Programación' AND c.topic = 'HTML y CSS Básico'
LIMIT 1;

-- ===================================
-- VERIFICATION
-- ===================================

SELECT 'Schema update completed! Composite keys preserved.' as Status;
DESCRIBE program_content;
SELECT 'Program table sample:' as Info;
SELECT id, name, title, type, status FROM program LIMIT 3;
SELECT 'Content table sample:' as Info;
SELECT id, topic, title, type FROM content LIMIT 3; 