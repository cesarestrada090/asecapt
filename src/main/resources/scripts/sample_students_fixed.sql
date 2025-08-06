-- Sample Students Data for ASECAPT (Complete Version)
-- This script creates sample persons and users with type = 3 (students)
-- Updated to include all user table columns

-- Insert sample persons for students
INSERT INTO person (first_name, last_name, document_number, document_type, phone_number, email, gender, birth_date) VALUES
('María', 'García', '12345678', 'DNI', '+51987654321', 'maria.garcia@email.com', 'F', '1995-03-15'),
('Carlos', 'López', '23456789', 'DNI', '+51987654322', 'carlos.lopez@email.com', 'M', '1992-07-22'),
('Ana', 'Rodríguez', '34567890', 'DNI', '+51987654323', 'ana.rodriguez@email.com', 'F', '1998-11-08'),
('Luis', 'Martínez', '45678901', 'DNI', '+51987654324', 'luis.martinez@email.com', 'M', '1996-01-30'),
('Sofia', 'Hernández', '56789012', 'DNI', '+51987654325', 'sofia.hernandez@email.com', 'F', '1994-09-14'),
('Diego', 'Torres', '67890123', 'DNI', '+51987654326', 'diego.torres@email.com', 'M', '1997-05-03'),
('Valentina', 'Flores', '78901234', 'DNI', '+51987654327', 'valentina.flores@email.com', 'F', '1999-12-19'),
('Sebastián', 'Vargas', '89012345', 'DNI', '+51987654328', 'sebastian.vargas@email.com', 'M', '1993-04-11'),
('Isabella', 'Mendoza', '90123456', 'DNI', '+51987654329', 'isabella.mendoza@email.com', 'F', '1991-08-27'),
('Andrés', 'Castillo', '01234567', 'DNI', '+51987654330', 'andres.castillo@email.com', 'M', '2000-02-06'),
('Camila', 'Ruiz', '11234567', 'CE', '+51987654331', 'camila.ruiz@email.com', 'F', '1995-10-12'),
('Mateo', 'Jiménez', '21234567', 'DNI', '+51987654332', 'mateo.jimenez@email.com', 'M', '1994-06-18'),
('Lucía', 'Morales', '31234567', 'DNI', '+51987654333', 'lucia.morales@email.com', 'F', '1997-03-25'),
('Santiago', 'Guerrero', '41234567', 'DNI', '+51987654334', 'santiago.guerrero@email.com', 'M', '1996-11-09'),
('Daniela', 'Cruz', '51234567', 'DNI', '+51987654335', 'daniela.cruz@email.com', 'F', '1998-07-16');

-- Create users for the students (type = 3) with complete column specification
-- Password for all students is 'student123' hashed with BCrypt: $2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO

-- Active students
SET @maria_id = (SELECT id FROM person WHERE document_number = '12345678' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('maria.garcia', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @maria_id, TRUE, TRUE, FALSE, 'NONE', NOW(), NOW());

SET @carlos_id = (SELECT id FROM person WHERE document_number = '23456789' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('carlos.lopez', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @carlos_id, TRUE, TRUE, FALSE, 'NONE', NOW(), NOW());

SET @ana_id = (SELECT id FROM person WHERE document_number = '34567890' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('ana.rodriguez', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @ana_id, TRUE, TRUE, FALSE, 'NONE', NOW(), NOW());

SET @luis_id = (SELECT id FROM person WHERE document_number = '45678901' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('luis.martinez', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @luis_id, TRUE, TRUE, FALSE, 'NONE', NOW(), NOW());

SET @sofia_id = (SELECT id FROM person WHERE document_number = '56789012' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('sofia.hernandez', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @sofia_id, TRUE, TRUE, FALSE, 'NONE', NOW(), NOW());

SET @diego_id = (SELECT id FROM person WHERE document_number = '67890123' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('diego.torres', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @diego_id, TRUE, TRUE, FALSE, 'NONE', NOW(), NOW());

SET @valentina_id = (SELECT id FROM person WHERE document_number = '78901234' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('valentina.flores', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @valentina_id, TRUE, TRUE, FALSE, 'NONE', NOW(), NOW());

SET @sebastian_id = (SELECT id FROM person WHERE document_number = '89012345' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('sebastian.vargas', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @sebastian_id, TRUE, TRUE, FALSE, 'NONE', NOW(), NOW());

SET @isabella_id = (SELECT id FROM person WHERE document_number = '90123456' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('isabella.mendoza', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @isabella_id, TRUE, TRUE, FALSE, 'NONE', NOW(), NOW());

SET @andres_id = (SELECT id FROM person WHERE document_number = '01234567' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('andres.castillo', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @andres_id, TRUE, TRUE, FALSE, 'NONE', NOW(), NOW());

SET @camila_id = (SELECT id FROM person WHERE document_number = '11234567' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('camila.ruiz', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @camila_id, TRUE, TRUE, FALSE, 'NONE', NOW(), NOW());

SET @mateo_id = (SELECT id FROM person WHERE document_number = '21234567' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('mateo.jimenez', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @mateo_id, TRUE, TRUE, FALSE, 'NONE', NOW(), NOW());

SET @lucia_id = (SELECT id FROM person WHERE document_number = '31234567' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('lucia.morales', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @lucia_id, TRUE, TRUE, FALSE, 'NONE', NOW(), NOW());

SET @santiago_id = (SELECT id FROM person WHERE document_number = '41234567' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('santiago.guerrero', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @santiago_id, TRUE, TRUE, FALSE, 'NONE', NOW(), NOW());

SET @daniela_id = (SELECT id FROM person WHERE document_number = '51234567' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('daniela.cruz', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @daniela_id, TRUE, TRUE, FALSE, 'NONE', NOW(), NOW());

-- Add some inactive students for testing
INSERT INTO person (first_name, last_name, document_number, document_type, phone_number, email, gender, birth_date) VALUES
('Roberto', 'Silva', '61234567', 'DNI', '+51987654336', 'roberto.silva@email.com', 'M', '1995-12-04'),
('Fernanda', 'Ramos', '71234567', 'DNI', '+51987654337', 'fernanda.ramos@email.com', 'F', '1993-09-21');

SET @roberto_id = (SELECT id FROM person WHERE document_number = '61234567' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('roberto.silva', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @roberto_id, TRUE, FALSE, FALSE, 'NONE', NOW(), NOW());

SET @fernanda_id = (SELECT id FROM person WHERE document_number = '71234567' LIMIT 1);
INSERT INTO user (username, password, type, person_id, is_email_verified, active, is_premium, premium_by, created_at, updated_at) 
VALUES ('fernanda.ramos', '$2a$10$K7Z.x3cX3bL9f5Fy4Q2vJe2tY.3nK5sL8fX4vZ9wR1pA6dT2mE7uO', 3, @fernanda_id, TRUE, FALSE, FALSE, 'NONE', NOW(), NOW());

-- Verify the data
SELECT 
    u.id,
    p.first_name,
    p.last_name, 
    p.document_number,
    p.email,
    u.username,
    u.active,
    u.is_email_verified,
    u.is_premium,
    u.premium_by,
    u.created_at
FROM user u 
JOIN person p ON u.person_id = p.id 
WHERE u.type = 3
ORDER BY p.first_name, p.last_name;