-- Create admin user for testing authentication
-- Password: admin123 (BCrypt encoded)

-- Insert person for admin user
INSERT INTO person (
    first_name, 
    last_name, 
    document_number, 
    document_type, 
    phone_number, 
    email, 
    gender, 
    birth_date, 
    bio,
    updated_at
) VALUES (
    'Administrador',
    'Sistema',
    '12345678',
    'DNI',
    '+51987654321',
    'admin@asecapt.com',
    'M',
    '1990-01-01',
    'Usuario administrador del sistema ASECAPT',
    NOW()
);

-- Insert admin user
-- Password: admin123 (BCrypt hash)
INSERT INTO user (
    username,
    password,
    type,
    person_id,
    is_email_verified,
    active,
    created_at,
    updated_at
) VALUES (
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zt.lvhyWVQ8S8GFGQ/YVjJ6uV7lODa', -- admin123
    1, -- Admin type
    LAST_INSERT_ID(),
    TRUE,
    TRUE,
    NOW(),
    NOW()
);

-- Create additional test admin user with different credentials
INSERT INTO person (
    first_name, 
    last_name, 
    document_number, 
    document_type, 
    phone_number, 
    email, 
    gender, 
    birth_date, 
    bio,
    updated_at
) VALUES (
    'Super',
    'Admin',
    '87654321',
    'DNI',
    '+51987654322',
    'superadmin@asecapt.com',
    'F',
    '1985-06-15',
    'Super administrador del sistema ASECAPT',
    NOW()
);

INSERT INTO user (
    username,
    password,
    type,
    person_id,
    is_email_verified,
    active,
    created_at,
    updated_at
) VALUES (
    'superadmin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zt.lvhyWVQ8S8GFGQ/YVjJ6uV7lODa', -- admin123
    1, -- Admin type
    LAST_INSERT_ID(),
    TRUE,
    TRUE,
    NOW(),
    NOW()
);