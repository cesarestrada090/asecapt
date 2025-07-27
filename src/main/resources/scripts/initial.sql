INSERT INTO person (first_name, last_name, document_number, phone_number, email)
VALUES ('Admin', 'Admin', '12345678', '+1234567890', 'admin@fitech.com');

-- Insertar un usuario asociado a la persona
-- Nota: La contraseña es 'testpass' hasheada con BCrypt
INSERT INTO user (username, password, type, person_id, is_email_verified)
VALUES (
    'admin',
    '$2a$10$Mr9oKYjp8kqkIdSsngj1IOhvFfVf6WChbaF4WnYuWkoBrpny9eAyO',
    1, -- Tipo: Cliente
    LAST_INSERT_ID(), -- ID de la persona insertada
    TRUE -- Email verificado
);