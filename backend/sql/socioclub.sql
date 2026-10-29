-- SocioClub: base de datos y datos de prueba (MySQL 8)
-- Borra la base y la vuelve a crear, así que sirve para dejar todo limpio antes de probar.
-- Correrlo desde Workbench o con el cliente mysql. Desde PowerShell no, porque rompe las tildes.

SET NAMES utf8mb4;

DROP DATABASE IF EXISTS socioclub_g13;

-- Con esta collation buscar "gomez" también encuentra "Gómez" (no distingue tildes ni mayúsculas)
CREATE DATABASE socioclub_g13 CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE socioclub_g13;

-- Tablas

CREATE TABLE socios (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    dni              VARCHAR(8)   NOT NULL,
    nombre           VARCHAR(50)  NOT NULL,
    apellido         VARCHAR(50)  NOT NULL,
    fecha_nacimiento DATE         NOT NULL,
    email            VARCHAR(100),
    telefono         VARCHAR(20),
    estado_cuota     VARCHAR(10)  NOT NULL DEFAULT 'AL_DIA',
    CONSTRAINT pk_socios PRIMARY KEY (id),
    CONSTRAINT uk_socios_dni UNIQUE (dni),
    CONSTRAINT ck_socios_dni CHECK (dni REGEXP '^[0-9]{7,8}$'),
    CONSTRAINT ck_socios_estado_cuota CHECK (estado_cuota IN ('AL_DIA', 'MOROSO'))
);

-- El cupo disponible no se guarda, se calcula contando las inscripciones activas
CREATE TABLE actividades (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    nombre        VARCHAR(60) NOT NULL,
    disciplina    VARCHAR(30) NOT NULL,
    profesor      VARCHAR(60) NOT NULL,
    dias          VARCHAR(40) NOT NULL,
    horario       VARCHAR(5)  NOT NULL,
    edad_minima   INT         NOT NULL,
    edad_maxima   INT         NOT NULL,
    cupo_maximo   INT         NOT NULL,
    cuota_mensual INT         NOT NULL,
    CONSTRAINT pk_actividades PRIMARY KEY (id),
    CONSTRAINT ck_actividades_edades CHECK (edad_minima >= 0 AND edad_maxima >= edad_minima),
    CONSTRAINT ck_actividades_cupo CHECK (cupo_maximo > 0),
    CONSTRAINT ck_actividades_cuota CHECK (cuota_mensual >= 0)
);

-- No lleva UNIQUE(socio_id, actividad_id) porque la baja es lógica y el socio se puede
-- volver a anotar. Para RN2 cuentan solo las ACTIVA.
CREATE TABLE inscripciones (
    id                BIGINT      NOT NULL AUTO_INCREMENT,
    socio_id          BIGINT      NOT NULL,
    actividad_id      BIGINT      NOT NULL,
    fecha_inscripcion DATE        NOT NULL,
    estado            VARCHAR(10) NOT NULL DEFAULT 'ACTIVA',
    CONSTRAINT pk_inscripciones PRIMARY KEY (id),
    CONSTRAINT fk_inscripciones_socio FOREIGN KEY (socio_id) REFERENCES socios (id),
    CONSTRAINT fk_inscripciones_actividad FOREIGN KEY (actividad_id) REFERENCES actividades (id),
    CONSTRAINT ck_inscripciones_estado CHECK (estado IN ('ACTIVA', 'BAJA'))
);

-- Actividades (sección 3.3 del enunciado)

INSERT INTO actividades (id, nombre, disciplina, profesor, dias, horario, edad_minima, edad_maxima, cupo_maximo, cuota_mensual) VALUES
(1, 'Fútbol infantil',   'Fútbol',    'Diego Ferreyra', 'Lun y Mié',      '18:00',  6, 12, 40, 18000),
(2, 'Vóley mixto',       'Vóley',     'Laura Ceballos', 'Mar y Jue',      '19:00', 13, 60, 24, 20000),
(3, 'Básquet juvenil',   'Básquet',   'Martín Oviedo',  'Lun y Vie',      '19:30', 12, 17, 20, 19000),
(4, 'Natación',          'Natación',  'Sofía Bustos',   'Mar y Jue',      '17:00',  5, 70, 30, 26000),
(5, 'Patín artístico',   'Patín',     'Carla Peralta',  'Mié y Vie',      '17:30',  5, 15, 25, 21000),
(6, 'Ajedrez',           'Ajedrez',   'Raúl Giménez',   'Sáb',            '10:00',  8, 99, 16,  9000),
(7, 'Funcional adultos', 'Fitness',   'Paula Luna',     'Lun, Mié y Vie', '20:30', 18, 65, 30, 22000),
(8, 'Yoga',              'Bienestar', 'Inés Arguello',  'Mar y Jue',      '9:00',  16, 99, 15, 17000);

-- Socios. Los ids 1 a 4 son los mismos que usa la app en modo local.
-- Las fechas de nacimiento están elegidas para que los casos de prueba sirvan hasta marzo de 2027.

INSERT INTO socios (id, dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota) VALUES
( 1, '40123456', 'Lucía',     'Fernández', '1997-05-14', 'lucia.fernandez@mail.com',    '351-555-0101', 'AL_DIA'),
( 2, '52345678', 'Mateo',     'Ríos',      '2014-08-03', 'familia.rios@mail.com',       '351-555-0102', 'AL_DIA'),
( 3, '9876543',  'Marta',     'Gómez',     '1958-02-20', 'marta.gomez@mail.com',        '351-555-0103', 'AL_DIA'),
( 4, '30111222', 'Jorge',     'Paz',       '1983-11-30', 'jorge.paz@mail.com',          '351-555-0104', 'MOROSO'),
( 5, '36123987', 'Sergio',    'Molina',    '1991-06-12', 'sergio.molina@mail.com',      '351-555-0105', 'AL_DIA'),
( 6, '24567890', 'Ricardo',   'Sosa',      '1975-09-08', 'ricardo.sosa@mail.com',       '351-555-0106', 'MOROSO'),
( 7, '50876543', 'Tomás',     'Álvarez',   '2012-05-20', 'tomas.alvarez@mail.com',      '351-555-0107', 'AL_DIA'),
( 8, '57654321', 'Emma',      'Quiroga',   '2019-07-10', 'emma.quiroga@mail.com',       '351-555-0108', 'AL_DIA'),
( 9, '6234567',  'Héctor',    'Bazán',     '1952-04-18', 'hector.bazan@mail.com',       '351-555-0109', 'AL_DIA'),
(10, '44123789', 'Camila',    'Herrera',   '2002-04-25', 'camila.herrera@mail.com',     '351-555-0110', 'AL_DIA'),
(11, '56012345', 'Martina',   'Ríos',      '2017-09-21', 'martina.rios@mail.com',       '351-555-0111', 'AL_DIA'),
(12, '50123987', 'Joaquín',   'Domínguez', '2010-06-30', 'joaquin.dominguez@mail.com',  '351-555-0112', 'AL_DIA'),
(13, '28976543', 'Ana',       'Gómez',     '1981-08-17', 'ana.gomez@mail.com',          '351-555-0113', 'AL_DIA'),
(14, '33654789', 'Pablo',     'Fernández', '1988-04-09', 'pablo.fernandez@mail.com',    '351-555-0114', 'AL_DIA'),
(15, '23876123', 'Lorena',    'Castro',    '1974-10-02', 'lorena.castro@mail.com',      '351-555-0115', 'AL_DIA'),
(16, '38912345', 'Federico',  'Morales',   '1995-07-23', 'federico.morales@mail.com',   '351-555-0116', 'AL_DIA'),
(17, '16543987', 'Silvia',    'Romero',    '1963-05-30', 'silvia.romero@mail.com',      '351-555-0117', 'AL_DIA'),
(18, '41765432', 'Hernán',    'Acosta',    '1999-09-14', 'hernan.acosta@mail.com',      '351-555-0118', 'MOROSO'),
(19, '47321654', 'Florencia', 'Medina',    '2007-08-05', 'florencia.medina@mail.com',   '351-555-0119', 'AL_DIA'),
(20, '48654321', 'Nicolás',   'Suárez',    '2009-05-11', 'nicolas.suarez@mail.com',     '351-555-0120', 'AL_DIA'),
(21, '11876543', 'Graciela',  'Ortiz',     '1956-06-03', 'graciela.ortiz@mail.com',     '351-555-0121', 'AL_DIA'),
(22, '54987321', 'Lautaro',   'Vega',      '2016-04-14', 'lautaro.vega@mail.com',       '351-555-0122', 'AL_DIA'),
(23, '51678234', 'Valentina', 'Ruiz',      '2013-09-02', 'valentina.ruiz@mail.com',     '351-555-0123', 'AL_DIA'),
(24, '58123456', 'Bruno',     'Pereyra',   '2020-05-27', 'bruno.pereyra@mail.com',      '351-555-0124', 'AL_DIA'),
(25, '31456789', 'Mariana',   'Benítez',   '1985-04-29', 'mariana.benitez@mail.com',    '351-555-0125', 'AL_DIA'),
(26, '20345678', 'Gabriel',   'Ponce',     '1968-08-12', 'gabriel.ponce@mail.com',      '351-555-0126', 'AL_DIA');

-- Inscripciones. Al cargar quedan libres: Fútbol 36, Vóley 20, Básquet 17, Natación 26,
-- Patín 22, Ajedrez 1, Funcional 27 y Yoga ninguno (está lleno).

INSERT INTO inscripciones (id, socio_id, actividad_id, fecha_inscripcion, estado) VALUES
-- 1 a 7: las mismas que la app en modo local
( 1,  1, 2, '2026-03-02', 'BAJA'),
( 2,  2, 1, '2026-03-05', 'ACTIVA'),
( 3,  2, 6, '2026-04-11', 'ACTIVA'),
( 4,  3, 4, '2026-02-20', 'ACTIVA'),
( 5,  3, 6, '2026-02-20', 'ACTIVA'),
( 6,  3, 8, '2026-03-01', 'ACTIVA'),
( 7,  4, 7, '2026-05-15', 'ACTIVA'),
-- Sergio Molina: ya tiene 3 (RN5)
( 8,  5, 2, '2026-03-09', 'ACTIVA'),
( 9,  5, 7, '2026-04-13', 'ACTIVA'),
(10,  5, 8, '2026-05-11', 'ACTIVA'),
-- Tomás Álvarez
(11,  7, 3, '2026-03-09', 'ACTIVA'),
-- Emma Quiroga
(12,  8, 5, '2026-03-09', 'ACTIVA'),
(13,  8, 4, '2026-04-13', 'ACTIVA'),
-- Héctor Bazán
(14,  9, 6, '2026-03-09', 'ACTIVA'),
(15,  9, 8, '2026-04-13', 'ACTIVA'),
-- Camila Herrera
(16, 10, 2, '2026-03-09', 'ACTIVA'),
-- Martina Ríos
(17, 11, 1, '2026-03-09', 'ACTIVA'),
(18, 11, 5, '2026-04-13', 'ACTIVA'),
-- Joaquín Domínguez (Yoga después de cumplir 16)
(19, 12, 3, '2026-03-09', 'ACTIVA'),
(20, 12, 8, '2026-07-06', 'ACTIVA'),
-- Ana Gómez
(21, 13, 8, '2026-03-09', 'ACTIVA'),
(22, 13, 6, '2026-04-13', 'ACTIVA'),
(23, 13, 7, '2026-05-11', 'ACTIVA'),
-- Pablo Fernández
(24, 14, 8, '2026-03-09', 'ACTIVA'),
(25, 14, 6, '2026-04-13', 'ACTIVA'),
-- Lorena Castro
(26, 15, 8, '2026-03-09', 'ACTIVA'),
(27, 15, 6, '2026-04-13', 'ACTIVA'),
-- Federico Morales
(28, 16, 8, '2026-03-09', 'ACTIVA'),
(29, 16, 6, '2026-04-13', 'ACTIVA'),
-- Silvia Romero
(30, 17, 8, '2026-03-09', 'ACTIVA'),
(31, 17, 6, '2026-04-13', 'ACTIVA'),
(32, 17, 4, '2026-05-11', 'ACTIVA'),
-- Hernán Acosta (moroso con actividades)
(33, 18, 8, '2026-03-09', 'ACTIVA'),
(34, 18, 6, '2026-04-13', 'ACTIVA'),
-- Florencia Medina
(35, 19, 8, '2026-03-09', 'ACTIVA'),
(36, 19, 6, '2026-04-13', 'ACTIVA'),
(37, 19, 2, '2026-05-11', 'ACTIVA'),
-- Nicolás Suárez
(38, 20, 3, '2026-03-09', 'ACTIVA'),
(39, 20, 8, '2026-04-13', 'ACTIVA'),
(40, 20, 6, '2026-05-11', 'ACTIVA'),
-- Graciela Ortiz
(41, 21, 4, '2026-03-09', 'ACTIVA'),
(42, 21, 8, '2026-04-13', 'ACTIVA'),
(43, 21, 6, '2026-05-11', 'ACTIVA'),
-- Lautaro Vega
(44, 22, 1, '2026-03-09', 'ACTIVA'),
(45, 22, 6, '2026-04-13', 'ACTIVA'),
-- Valentina Ruiz (Vóley después de cumplir 13)
(46, 23, 5, '2026-03-09', 'ACTIVA'),
(47, 23, 2, '2026-09-07', 'ACTIVA'),
-- Bruno Pereyra (Fútbol después de cumplir 6)
(48, 24, 1, '2026-06-01', 'ACTIVA'),
-- Mariana Benítez
(49, 25, 8, '2026-03-09', 'ACTIVA'),
(50, 25, 6, '2026-04-13', 'ACTIVA'),
-- Gabriel Ponce
(51, 26, 8, '2026-03-09', 'ACTIVA'),
(52, 26, 6, '2026-04-13', 'ACTIVA'),
-- Bajas: no tienen que contar para el cupo, RN2 ni RN5
(53, 10, 6, '2026-04-13', 'BAJA'),
(54,  5, 4, '2026-04-13', 'BAJA'),
(55,  1, 8, '2026-05-11', 'BAJA');

-- Usuario del backend (el mismo de application.properties).
-- Va al final para que, si esto falla por permisos, los datos queden cargados igual.

CREATE USER IF NOT EXISTS 'socioclub_g13'@'localhost' IDENTIFIED BY 'SocioClub_2026';
CREATE USER IF NOT EXISTS 'socioclub_g13'@'127.0.0.1' IDENTIFIED BY 'SocioClub_2026';
ALTER USER 'socioclub_g13'@'localhost' IDENTIFIED BY 'SocioClub_2026';
ALTER USER 'socioclub_g13'@'127.0.0.1' IDENTIFIED BY 'SocioClub_2026';
GRANT ALL PRIVILEGES ON socioclub_g13.* TO 'socioclub_g13'@'localhost';
GRANT ALL PRIVILEGES ON socioclub_g13.* TO 'socioclub_g13'@'127.0.0.1';
