-- Scalable script for programs and contents

CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE program (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    duration VARCHAR(50) NOT NULL,
    hours INT NOT NULL,
    credits INT NOT NULL,
    category_id INT,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE content (
    id INT AUTO_INCREMENT PRIMARY KEY,
    topic_number INT NOT NULL,
    topic VARCHAR(255) NOT NULL,
    description TEXT,
    parent_topic_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_topic_id) REFERENCES content(id)
);

CREATE TABLE program_content (
    program_id INT NOT NULL,
    content_id INT NOT NULL,
    PRIMARY KEY (program_id, content_id),
    FOREIGN KEY (program_id) REFERENCES program(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES content(id) ON DELETE CASCADE
);

-- Insert programs (Spanish names and details)
INSERT INTO program (name, duration, hours, credits) VALUES
('Salud ocupacional y medicina en el trabajo', '12 meses', 1200, 48),
('ALTO RIESGO OBSTÉTRICO', '12 meses', 1200, 48),
('NUTRICIÓN CLÍNICA', '12 meses', 1200, 48),
('ENFERMERÍA DE URGENCIAS Y EMERGENCIAS', '12 meses', 1200, 48),
('URGENCIAS Y EMERGENCIAS MÉDICAS', '12 meses', 1200, 48),
('SEGURIDAD, SALUD OCUPACIONAL Y MEDIO AMBIENTE SSOMA', '12 meses', 1200, 48),
('CRECIMIENTO Y DESARROLLO E INMUNIZACIONES EN EL NIÑO', '12 meses', 1200, 48),
('ALTA DIRECCIÓN EN SALUD PÚBLICA Y HOSPITALES', '12 meses', 1200, 48),
('HEMOTERAPIA Y BANCO DE SANGRE', '12 meses', 1200, 48),
('GESTIÓN PÚBLICA', '12 meses', 1200, 48);

-- Insert contents (Spanish topics, ordered for program_content relation)
INSERT INTO content (topic_number, topic) VALUES
-- Salud ocupacional y medicina en el trabajo
(1, 'Integración humana, comunicación y presentaciones orales'),
(2, 'Proceso salud y trabajo en servicios médicos ocupacionales.'),
(3, 'Higiene y seguridad ocupacional.'),
(4, 'Toxicología ocupacional.'),
(5, 'Enfermería ocupacional aplicada.'),
(6, 'Ética en salud ocupacional.'),
(7, 'Administración de los servicios en enfermería ocupacional.'),
(8, 'Legislación aplicada a la enfermería ocupacional.'),
(9, 'Ergonomía Aplicada a la Enfermería Ocupacional.'),
(10, 'Sistemas de Vigilancia Epidemiológica en Servicios de Enfermería Ocupacional.'),
(11, 'Psicología del trabajo.'),
(12, 'Salud ocupacional y vigilancia en salud.'),
-- ALTO RIESGO OBSTÉTRICO
(1, 'Control prenatal.'),
(2, 'Nutrición materna y embarazo.'),
(3, 'Complicaciones del embarazo I.'),
(4, 'Complicaciones del embarazo II.'),
(5, 'Patología médica y embarazo I.'),
(6, 'Patología médica y embarazo II.'),
(7, 'Infecciones.'),
(8, 'Patología fetal.'),
(9, 'Evaluación de la condición fetal.'),
(10, 'Complicaciones post parto.'),
(11, 'Gestantes de alto riesgo obstétrico.'),
(12, 'Parto prematuro.'),
-- NUTRICIÓN CLÍNICA
(1, 'Evaluación del nivel nutricional.'),
(2, 'Nutrición del paciente: enfermedades digestivas.'),
(3, 'Nutrición del paciente: Hepatología, vía biliar y ginecológica.'),
(4, 'Nutrición del paciente: endocrinología, nutrición del paciente.'),
(5, 'Nefrología, neumología e infección por VIH.'),
(6, 'Nutrición del paciente: cardiología y hematología.'),
(7, 'Nutrición del paciente: neurología.'),
(8, 'Nutrición del paciente: oncología.'),
(9, 'Nutrición del paciente: cirugía.'),
(10, 'Nutrición del paciente: psiquiatría.'),
(11, 'Nutrición geriátrica.'),
(12, 'Nutrición en patología ósea y articular.'),
-- ENFERMERÍA DE URGENCIAS Y EMERGENCIAS
(1, 'Urgencias cardiovasculares – 1.'),
(2, 'Urgencias cardiovasculares – 2.'),
(3, 'Urgencias cardiovasculares – 3.'),
(4, 'Urgencias respiratorias.'),
(5, 'Urgencias neurológicas.'),
(6, 'Urgencias abdominales.'),
(7, 'Urgencias hipoglucemias e hiperglucemias.'),
(8, 'Insuficiencia renal.'),
(9, 'Transfusión de sangre y hemoderivados.'),
(10, 'Inmunodeficiencias.'),
(11, 'Lesiones por radiaciones ionizantes, electricidad y rayos.'),
(12, 'Traumatismos.'),
-- URGENCIAS Y EMERGENCIAS MÉDICAS
(1, 'Reanimación básica y avanzada del paciente pediátrico.'),
(2, 'Cardiovascular.'),
(3, 'Insuficiencia cardiaca congestiva, insuficiencia respiratoria aguda, shock, edema agudo pulmonar y embolia pulmonar.'),
(4, 'Asma, neumonía y pancreatitis aguda.'),
(5, 'Fallo hepático fulminante e infección intraabdominal.'),
(6, 'Urgencias urológicas.'),
(7, 'Infarto agudo al miocardio.'),
(8, 'Tétanos, meningitis, epilepsia y enfermedades, cerebro vascular.'),
(9, 'Reanimación avanzada de paciente intoxicado.'),
(10, 'Hemorragias digestivas.'),
(11, 'Manejo integral del paciente clínico.'),
(12, 'Complicaciones urgentes de un paciente diabético.'),
-- SEGURIDAD, SALUD OCUPACIONAL Y MEDIO AMBIENTE SSOMA
(1, 'Marco legal y situación actual en seguridad y salud ocupacional en el Perú.'),
(2, 'Doctrina y fundamento de la salud ocupacional.'),
(3, 'Epidemiología en seguridad y salud ocupacional.'),
(4, 'Higiene ocupacional por sectores económicos de servicios y productivos. Manejo de sustancias químicas y residuos peligrosos.'),
(5, 'Identificación del peligro. Análisis de vulnerabilidad y evaluación de riesgos.'),
(6, 'Ergonomía y Psicosociología. Incapacidad laboral.'),
(7, 'Análisis y gestión de la prevención de riesgos ocupacionales.'),
(8, 'Ergonomía y Psicosociología. Incapacidad laboral.'),
(9, 'Accidentes laborales, Enfermedades profesionales y Toxicología industrial.'),
(10, 'Auditorías, inspecciones e investigaciones en la gestión de la seguridad del trabajo.'),
(11, 'Implementación del Sistema de Gestión de Seguridad y Salud Ocupacional.'),
(12, 'Gestión en la investigación de incidentes. Accidentes de trabajo y enfermedades ocupacionales.'),
-- CRECIMIENTO Y DESARROLLO E INMUNIZACIONES EN EL NIÑO
(1, 'Aspectos generales del crecimiento – 1.'),
(2, 'Aspectos generales del crecimiento – 2.'),
(3, 'Atención del niño durante las distintas etapas de crecimiento y desarrollo – 1.'),
(4, 'Atención del niño durante las distintas etapas de crecimiento y desarrollo – 2.'),
(5, 'Evaluación del desarrollo del niño.'),
(6, 'Desarrollo de la niña y del niño.'),
(7, 'Educación en salud.'),
(8, 'Inmunizaciones.'),
(9, 'Alcances en la estimulación pre natal y temprana.'),
(10, 'Fundamentos necrológicos de la maduración infantil.'),
(11, 'Fundamentos necrológicos de los reflejos en la infancia.'),
(12, 'Violencia infantil.'),
-- ALTA DIRECCIÓN EN SALUD PÚBLICA Y HOSPITALES
(1, 'Salud pública, epidemiologia, cultura y salud: elementos estadísticos.'),
(2, 'Organización de la salud pública.'),
(3, 'Nutrición, salud mental, saneamiento ambiental.'),
(4, 'Epidemiologia de los servicios locales de salud.'),
(5, 'Vigilancia epidemiológica.'),
(6, 'Administración de programas de la salud ambiental'),
(7, 'Manejo de desechos médicos en países en desarrollo'),
(8, 'Mejoramiento de la calidad de los servicios de salud'),
(9, 'Monitoreo e implantación de programas de garantía de la calidad.'),
(10, 'Comunicación, recursos humanos, motivación y planificación estratégica.'),
(11, 'Dirección, liderazgo y coordinación en la administración de clínicas y hospitales.'),
(12, 'Toma de decisiones, control, sistemas de información y estructura.'),
-- HEMOTERAPIA Y BANCO DE SANGRE
(1, 'Estrategias de donación voluntaria de sangre.'),
(2, 'Selección del donante de sangre. Pruebas sanguíneas. Colecta y preparación de la sangre.'),
(3, 'Tipos de preparados. Transporte y administración de la sangre.'),
(4, 'Enfermedades infecciosas hemotransmisibles.'),
(5, 'Reacciones transfusionales.'),
(6, 'Diagnóstico en laboratorio de la hemofilia y otros trastornos de la coagulación.'),
(7, 'Trasplante de médula ósea.'),
(8, 'Inmunohematología.'),
(9, 'Uso racional de hemocomponentes.'),
(10, 'Aféresis y terapia regenerativa.'),
(11, 'Control de calidad en banco de sangre.'),
(12, 'Gestión de calidad en banco de sangre.'),
-- GESTIÓN PÚBLICA
(1, 'Política y gestión pública.'),
(2, 'Doctrina general del estado – 1:   El fin del estado.'),
(3, 'Doctrina general del estado – 2:  La soberanía.'),
(4, 'Sistemas complejos y gestión pública.'),
(5, 'Evaluación del desempeño de gestión de la deuda pública.'),
(6, 'Descentralización y desarrollo.'),
(7, 'Gerencia pública – 1:  eficiencia, eficacia y gestión de proyectos.'),
(8, 'Gerencia pública – 1: gestión de proyectos y evaluación estratégica global.'),
(9, 'Modernización de la gestión pública.'),
(10, 'La gerencia social en las relaciones entre el estado y la sociedad.'),
(11, 'Gestión pública regional y municipal -1: Recursos económicos y financieros.'),
(12, 'Gestión pública regional y municipal -2:  recursos humanos e inversión pública.');

-- Relate programs and contents
-- Salud ocupacional y medicina en el trabajo (program_id = 1, content_id = 1-12)
INSERT INTO program_content (program_id, content_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12);

-- ALTO RIESGO OBSTÉTRICO (program_id = 2, content_id = 13-24)
INSERT INTO program_content (program_id, content_id) VALUES
(2, 13), (2, 14), (2, 15), (2, 16), (2, 17), (2, 18), (2, 19), (2, 20), (2, 21), (2, 22), (2, 23), (2, 24);

-- NUTRICIÓN CLÍNICA (program_id = 3, content_id = 25-36)
INSERT INTO program_content (program_id, content_id) VALUES
(3, 25), (3, 26), (3, 27), (3, 28), (3, 29), (3, 30), (3, 31), (3, 32), (3, 33), (3, 34), (3, 35), (3, 36);

-- ENFERMERÍA DE URGENCIAS Y EMERGENCIAS (program_id = 4, content_id = 37-48)
INSERT INTO program_content (program_id, content_id) VALUES
(4, 37), (4, 38), (4, 39), (4, 40), (4, 41), (4, 42), (4, 43), (4, 44), (4, 45), (4, 46), (4, 47), (4, 48);

-- URGENCIAS Y EMERGENCIAS MÉDICAS (program_id = 5, content_id = 49-60)
INSERT INTO program_content (program_id, content_id) VALUES
(5, 49), (5, 50), (5, 51), (5, 52), (5, 53), (5, 54), (5, 55), (5, 56), (5, 57), (5, 58), (5, 59), (5, 60);

-- SEGURIDAD, SALUD OCUPACIONAL Y MEDIO AMBIENTE SSOMA (program_id = 6, content_id = 61-72)
INSERT INTO program_content (program_id, content_id) VALUES
(6, 61), (6, 62), (6, 63), (6, 64), (6, 65), (6, 66), (6, 67), (6, 68), (6, 69), (6, 70), (6, 71), (6, 72);

-- CRECIMIENTO Y DESARROLLO E INMUNIZACIONES EN EL NIÑO (program_id = 7, content_id = 73-84)
INSERT INTO program_content (program_id, content_id) VALUES
(7, 73), (7, 74), (7, 75), (7, 76), (7, 77), (7, 78), (7, 79), (7, 80), (7, 81), (7, 82), (7, 83), (7, 84);

-- ALTA DIRECCIÓN EN SALUD PÚBLICA Y HOSPITALES (program_id = 8, content_id = 85-96)
INSERT INTO program_content (program_id, content_id) VALUES
(8, 85), (8, 86), (8, 87), (8, 88), (8, 89), (8, 90), (8, 91), (8, 92), (8, 93), (8, 94), (8, 95), (8, 96);

-- HEMOTERAPIA Y BANCO DE SANGRE (program_id = 9, content_id = 97-108)
INSERT INTO program_content (program_id, content_id) VALUES
(9, 97), (9, 98), (9, 99), (9, 100), (9, 101), (9, 102), (9, 103), (9, 104), (9, 105), (9, 106), (9, 107), (9, 108);

-- GESTIÓN PÚBLICA (program_id = 10, content_id = 109-120)
INSERT INTO program_content (program_id, content_id) VALUES
(10, 109), (10, 110), (10, 111), (10, 112), (10, 113), (10, 114), (10, 115), (10, 116), (10, 117), (10, 118), (10, 119), (10, 120);

