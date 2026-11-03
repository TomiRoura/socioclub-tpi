-- =====================================================================
-- SocioClub · Club Social y Deportivo Los Algarrobos
-- Script de creación de la base, tablas, usuario de la app y datos de prueba.
--
-- Ejecutar con root, desde cmd o Workbench (NUNCA desde PowerShell: rompe las tildes):
--   mysql -u root -p < backend\sql\socioclub.sql
-- Volver a correrlo borra la base y la carga de nuevo (conviene antes de cada demo).
-- =====================================================================

SET NAMES utf8mb4;

DROP DATABASE IF EXISTS socioclub_g13;
-- utf8mb4_0900_ai_ci: no distingue tildes ni mayúsculas ("gomez" encuentra "Gómez").
CREATE DATABASE socioclub_g13 CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE socioclub_g13;

-- ---------------------------------------------------------------------
-- Tablas
-- ---------------------------------------------------------------------
CREATE TABLE actividades (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    nombre        VARCHAR(60)  NOT NULL,
    disciplina    VARCHAR(30)  NOT NULL,
    profesor      VARCHAR(60)  NOT NULL,
    dias          VARCHAR(40)  NOT NULL,
    horario       VARCHAR(5)   NOT NULL,
    edad_minima   INT          NOT NULL,
    edad_maxima   INT          NOT NULL,
    cupo_maximo   INT          NOT NULL,
    cuota_mensual INT          NOT NULL,
    CONSTRAINT pk_actividades PRIMARY KEY (id),
    CONSTRAINT ck_actividades_edades CHECK (edad_minima >= 0 AND edad_minima <= edad_maxima),
    CONSTRAINT ck_actividades_cupo   CHECK (cupo_maximo > 0),
    CONSTRAINT ck_actividades_cuota  CHECK (cuota_mensual >= 0)
) ENGINE = InnoDB;

CREATE TABLE socios (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    dni              VARCHAR(8)   NOT NULL,
    nombre           VARCHAR(50)  NOT NULL,
    apellido         VARCHAR(50)  NOT NULL,
    fecha_nacimiento DATE         NOT NULL,
    email            VARCHAR(100) NULL,
    telefono         VARCHAR(20)  NULL,
    estado_cuota     VARCHAR(10)  NOT NULL DEFAULT 'AL_DIA',
    CONSTRAINT pk_socios PRIMARY KEY (id),
    CONSTRAINT uk_socios_dni UNIQUE (dni),
    CONSTRAINT ck_socios_dni    CHECK (dni REGEXP '^[0-9]{7,8}$'),
    CONSTRAINT ck_socios_estado CHECK (estado_cuota IN ('AL_DIA', 'MOROSO'))
) ENGINE = InnoDB;

-- Sin UNIQUE(socio_id, actividad_id): la baja es lógica y un socio puede volver a una actividad que dejó.
CREATE TABLE inscripciones (
    id                BIGINT      NOT NULL AUTO_INCREMENT,
    socio_id          BIGINT      NOT NULL,
    actividad_id      BIGINT      NOT NULL,
    fecha_inscripcion DATE        NOT NULL,
    estado            VARCHAR(10) NOT NULL DEFAULT 'ACTIVA',
    CONSTRAINT pk_inscripciones PRIMARY KEY (id),
    CONSTRAINT fk_inscripciones_socio     FOREIGN KEY (socio_id)     REFERENCES socios (id),
    CONSTRAINT fk_inscripciones_actividad FOREIGN KEY (actividad_id) REFERENCES actividades (id),
    CONSTRAINT ck_inscripciones_estado CHECK (estado IN ('ACTIVA', 'BAJA'))
) ENGINE = InnoDB;

CREATE INDEX ix_inscripciones_socio_estado     ON inscripciones (socio_id, estado);
CREATE INDEX ix_inscripciones_actividad_estado ON inscripciones (actividad_id, estado);

-- ---------------------------------------------------------------------
-- Actividades (sección 3.3 del enunciado)
-- ---------------------------------------------------------------------
INSERT INTO actividades (id, nombre, disciplina, profesor, dias, horario, edad_minima, edad_maxima, cupo_maximo, cuota_mensual) VALUES
(1, 'Fútbol infantil',   'Fútbol',    'Diego Ferreyra', 'Lun y Mié',      '18:00',  6, 12, 40, 18000),
(2, 'Vóley mixto',       'Vóley',     'Laura Ceballos', 'Mar y Jue',      '19:00', 13, 60, 24, 20000),
(3, 'Básquet juvenil',   'Básquet',   'Martín Oviedo',  'Lun y Vie',      '19:30', 12, 17, 20, 19000),
(4, 'Natación',          'Natación',  'Sofía Bustos',   'Mar y Jue',      '17:00',  5, 70, 30, 26000),
(5, 'Patín artístico',   'Patín',     'Carla Peralta',  'Mié y Vie',      '17:30',  5, 15, 25, 21000),
(6, 'Ajedrez',           'Ajedrez',   'Raúl Giménez',   'Sáb',            '10:00',  8, 99, 16,  9000),
(7, 'Funcional adultos', 'Fitness',   'Paula Luna',     'Lun, Mié y Vie', '20:30', 18, 65, 30, 22000),
(8, 'Yoga',              'Bienestar', 'Inés Arguello',  'Mar y Jue',      '9:00',  16, 99, 15, 17000);

-- ---------------------------------------------------------------------
-- Socios de prueba (26, 3 morosos). Los 1 a 4 son los mismos del modo local de la app.
-- Las fechas mantienen los casos de prueba hasta marzo de 2027.
-- ---------------------------------------------------------------------
INSERT INTO socios (id, dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota) VALUES
( 1, '40123456', 'Lucía',     'Fernández',  '1997-05-14', 'lucia.fernandez@mail.com',   '351-555-0101', 'AL_DIA'),
( 2, '52345678', 'Mateo',     'Ríos',       '2014-08-03', 'familia.rios@mail.com',      '351-555-0102', 'AL_DIA'),
( 3, '9876543',  'Marta',     'Gómez',      '1958-02-20', 'marta.gomez@mail.com',       '351-555-0103', 'AL_DIA'),
( 4, '30111222', 'Jorge',     'Paz',        '1983-11-30', 'jorge.paz@mail.com',         '351-555-0104', 'MOROSO'),
( 5, '24567890', 'Ricardo',   'Sosa',       '1975-09-12', 'ricardo.sosa@mail.com',      '351-555-0105', 'MOROSO'),
( 6, '36123987', 'Sergio',    'Molina',     '1991-06-25', 'sergio.molina@mail.com',     '351-555-0106', 'AL_DIA'),
( 7, '50876543', 'Tomás',     'Álvarez',    '2011-05-19', 'familia.alvarez@mail.com',   '351-555-0107', 'AL_DIA'),
( 8, '57654321', 'Emma',      'Quiroga',    '2018-09-02', 'familia.quiroga@mail.com',   '351-555-0108', 'AL_DIA'),
( 9, '51678234', 'Valentina', 'Ruiz',       '2013-07-08', 'familia.ruiz@mail.com',      '351-555-0109', 'AL_DIA'),
(10, '44123789', 'Camila',    'Herrera',    '2002-10-15', 'camila.herrera@mail.com',    '351-555-0110', 'AL_DIA'),
(11, '20345678', 'Gabriel',   'Ponce',      '1968-04-10', 'gabriel.ponce@mail.com',     '351-555-0111', 'AL_DIA'),
(12, '55234111', 'Benjamín',  'Torres',     '2016-04-22', 'familia.torres@mail.com',    '351-555-0112', 'AL_DIA'),
(13, '56345222', 'Sofía',     'Medina',     '2017-09-14', 'familia.medina@mail.com',    '351-555-0113', 'AL_DIA'),
(14, '58456333', 'Joaquín',   'Vera',       '2019-05-30', 'familia.vera@mail.com',      '351-555-0114', 'AL_DIA'),
(15, '49567444', 'Franco',    'Aguirre',    '2009-12-05', 'franco.aguirre@mail.com',    '351-555-0115', 'AL_DIA'),
(16, '39678555', 'Agustina',  'Carranza',   '1995-03-18', 'agustina.carranza@mail.com', '351-555-0116', 'AL_DIA'),
(17, '33789666', 'Nicolás',   'Bustamante', '1988-07-07', 'nicolas.bustamante@mail.com','351-555-0117', 'MOROSO'),
(18, '27890777', 'Florencia', 'Ledesma',    '1979-11-21', 'florencia.ledesma@mail.com', '351-555-0118', 'AL_DIA'),
(19, '31901888', 'Pablo',     'Cáceres',    '1985-01-09', 'pablo.caceres@mail.com',     '351-555-0119', 'AL_DIA'),
(20, '35012999', 'Romina',    'Vega',       '1990-08-27', 'romina.vega@mail.com',       '351-555-0120', 'AL_DIA'),
(21, '6123456',  'Héctor',    'Domínguez',  '1950-02-14', 'hector.dominguez@mail.com',  '351-555-0121', 'AL_DIA'),
(22, '14234567', 'Silvia',    'Romero',     '1962-06-03', 'silvia.romero@mail.com',     '351-555-0122', 'AL_DIA'),
(23, '23345678', 'Ana',       'Villalba',   '1973-10-30', 'ana.villalba@mail.com',      '351-555-0123', 'AL_DIA'),
(24, '42456789', 'Esteban',   'Quinteros',  '1999-12-12', 'esteban.quinteros@mail.com', '351-555-0124', 'AL_DIA'),
(25, '43567890', 'Carolina',  'Funes',      '2001-04-04', 'carolina.funes@mail.com',    '351-555-0125', 'AL_DIA'),
(26, '16678901', 'Raúl',      'Ortiz',      '1957-09-09', 'raul.ortiz@mail.com',        '351-555-0126', 'AL_DIA');

-- ---------------------------------------------------------------------
-- Inscripciones (55: 51 ACTIVA y 4 BAJA). Las 1 a 7 son las del modo local de la app.
-- Lugares libres al cargar: Fútbol 36/40, Vóley 20/24, Básquet 17/20, Natación 26/30,
-- Patín 22/25, Ajedrez 1/16 (casi completa), Funcional 27/30, Yoga 0/15 (sin cupo).
-- ---------------------------------------------------------------------
INSERT INTO inscripciones (id, socio_id, actividad_id, fecha_inscripcion, estado) VALUES
-- Modo local de la app
( 1,  1, 2, '2026-03-02', 'BAJA'),
( 2,  2, 1, '2026-03-05', 'ACTIVA'),
( 3,  2, 6, '2026-04-11', 'ACTIVA'),
( 4,  3, 4, '2026-02-20', 'ACTIVA'),
( 5,  3, 6, '2026-02-20', 'ACTIVA'),
( 6,  3, 8, '2026-03-01', 'ACTIVA'),
( 7,  4, 7, '2026-05-15', 'ACTIVA'),
-- Ricardo Sosa (moroso): solo una baja en Ajedrez (si se cuentan las BAJA, Ajedrez aparece lleno)
( 8,  5, 6, '2026-03-10', 'BAJA'),
-- Sergio Molina: 3 actividades (RN5)
( 9,  6, 2, '2026-03-03', 'ACTIVA'),
(10,  6, 7, '2026-03-03', 'ACTIVA'),
(11,  6, 8, '2026-03-04', 'ACTIVA'),
-- Tomás Álvarez
(12,  7, 3, '2026-03-06', 'ACTIVA'),
(13,  7, 6, '2026-03-07', 'ACTIVA'),
-- Emma Quiroga
(14,  8, 5, '2026-03-09', 'ACTIVA'),
(15,  8, 4, '2026-03-09', 'ACTIVA'),
-- Valentina Ruiz
(16,  9, 5, '2026-03-11', 'ACTIVA'),
(17,  9, 3, '2026-03-12', 'ACTIVA'),
-- Camila Herrera
(18, 10, 2, '2026-03-13', 'ACTIVA'),
(19, 10, 6, '2026-03-13', 'ACTIVA'),
(20, 10, 4, '2026-02-15', 'BAJA'),
-- Gabriel Ponce
(21, 11, 8, '2026-03-16', 'ACTIVA'),
(22, 11, 6, '2026-03-16', 'ACTIVA'),
-- Benjamín Torres
(23, 12, 1, '2026-03-17', 'ACTIVA'),
(24, 12, 4, '2026-03-17', 'ACTIVA'),
-- Sofía Medina
(25, 13, 1, '2026-03-18', 'ACTIVA'),
(26, 13, 5, '2026-03-18', 'ACTIVA'),
-- Joaquín Vera
(27, 14, 1, '2026-03-19', 'ACTIVA'),
(28, 14, 5, '2026-02-19', 'BAJA'),
-- Franco Aguirre
(29, 15, 8, '2026-03-20', 'ACTIVA'),
(30, 15, 3, '2026-03-20', 'ACTIVA'),
(31, 15, 6, '2026-03-21', 'ACTIVA'),
-- Agustina Carranza
(32, 16, 8, '2026-03-23', 'ACTIVA'),
(33, 16, 2, '2026-03-23', 'ACTIVA'),
(34, 16, 6, '2026-03-24', 'ACTIVA'),
-- Nicolás Bustamante (moroso con actividades: puede verlas y darse de baja)
(35, 17, 8, '2026-03-25', 'ACTIVA'),
(36, 17, 6, '2026-03-25', 'ACTIVA'),
-- Florencia Ledesma
(37, 18, 8, '2026-03-26', 'ACTIVA'),
(38, 18, 4, '2026-03-26', 'ACTIVA'),
(39, 18, 6, '2026-03-27', 'ACTIVA'),
-- Pablo Cáceres
(40, 19, 8, '2026-03-30', 'ACTIVA'),
(41, 19, 7, '2026-03-30', 'ACTIVA'),
(42, 19, 6, '2026-03-31', 'ACTIVA'),
-- Romina Vega
(43, 20, 8, '2026-04-01', 'ACTIVA'),
(44, 20, 2, '2026-04-01', 'ACTIVA'),
(45, 20, 6, '2026-04-02', 'ACTIVA'),
-- Héctor Domínguez
(46, 21, 8, '2026-04-06', 'ACTIVA'),
(47, 21, 6, '2026-04-06', 'ACTIVA'),
-- Silvia Romero
(48, 22, 8, '2026-04-07', 'ACTIVA'),
(49, 22, 6, '2026-04-07', 'ACTIVA'),
-- Ana Villalba
(50, 23, 8, '2026-04-08', 'ACTIVA'),
(51, 23, 6, '2026-04-08', 'ACTIVA'),
-- Esteban Quinteros
(52, 24, 8, '2026-04-09', 'ACTIVA'),
(53, 24, 6, '2026-04-09', 'ACTIVA'),
-- Carolina Funes y Raúl Ortiz
(54, 25, 8, '2026-04-13', 'ACTIVA'),
(55, 26, 8, '2026-04-14', 'ACTIVA');

-- ---------------------------------------------------------------------
-- Usuario de la aplicación (solo puede usar esta base)
-- ---------------------------------------------------------------------
CREATE USER IF NOT EXISTS 'socioclub_g13'@'localhost' IDENTIFIED BY 'SocioClub_2026';
GRANT ALL PRIVILEGES ON socioclub_g13.* TO 'socioclub_g13'@'localhost';
FLUSH PRIVILEGES;
