// Datos de prueba para el modo local (REACT_APP_USAR_DATOS_LOCALES). Tienen la misma forma
// que el JSON de la API (ver CONTRATO-API.md). Solo los usa servicios/apiLocal.js.

// Las 8 actividades oficiales del club (consigna, sección 3.3).
// cupoDisponible es inventado para la demo; Yoga queda en 0 para mostrar "Sin cupo".
export const actividadesIniciales = [
  { id: 1, nombre: 'Fútbol infantil', disciplina: 'Fútbol', profesor: 'Diego Ferreyra', dias: 'Lun y Mié', horario: '18:00', edadMinima: 6, edadMaxima: 12, cupoMaximo: 40, cupoDisponible: 12, cuotaMensual: 18000 },
  { id: 2, nombre: 'Vóley mixto', disciplina: 'Vóley', profesor: 'Laura Ceballos', dias: 'Mar y Jue', horario: '19:00', edadMinima: 13, edadMaxima: 60, cupoMaximo: 24, cupoDisponible: 5, cuotaMensual: 20000 },
  { id: 3, nombre: 'Básquet juvenil', disciplina: 'Básquet', profesor: 'Martín Oviedo', dias: 'Lun y Vie', horario: '19:30', edadMinima: 12, edadMaxima: 17, cupoMaximo: 20, cupoDisponible: 8, cuotaMensual: 19000 },
  { id: 4, nombre: 'Natación', disciplina: 'Natación', profesor: 'Sofía Bustos', dias: 'Mar y Jue', horario: '17:00', edadMinima: 5, edadMaxima: 70, cupoMaximo: 30, cupoDisponible: 3, cuotaMensual: 26000 },
  { id: 5, nombre: 'Patín artístico', disciplina: 'Patín', profesor: 'Carla Peralta', dias: 'Mié y Vie', horario: '17:30', edadMinima: 5, edadMaxima: 15, cupoMaximo: 25, cupoDisponible: 10, cuotaMensual: 21000 },
  { id: 6, nombre: 'Ajedrez', disciplina: 'Ajedrez', profesor: 'Raúl Giménez', dias: 'Sáb', horario: '10:00', edadMinima: 8, edadMaxima: 99, cupoMaximo: 16, cupoDisponible: 7, cuotaMensual: 9000 },
  { id: 7, nombre: 'Funcional adultos', disciplina: 'Fitness', profesor: 'Paula Luna', dias: 'Lun, Mié y Vie', horario: '20:30', edadMinima: 18, edadMaxima: 65, cupoMaximo: 30, cupoDisponible: 14, cuotaMensual: 22000 },
  { id: 8, nombre: 'Yoga', disciplina: 'Bienestar', profesor: 'Inés Arguello', dias: 'Mar y Jue', horario: '9:00', edadMinima: 16, edadMaxima: 99, cupoMaximo: 15, cupoDisponible: 0, cuotaMensual: 17000 },
];

// Socios de prueba (DNI para ingresar en el README).
export const sociosIniciales = [
  // Al día, sin actividades activas (tiene una dada de baja). 29 años.
  { id: 1, dni: '40123456', nombre: 'Lucía', apellido: 'Fernández', fechaNacimiento: '1997-05-14', email: 'lucia.fernandez@mail.com', telefono: '351-555-0101', estadoCuota: 'AL_DIA' },
  // Al día, con 2 actividades. 12 años (cumple el 3 de agosto).
  { id: 2, dni: '52345678', nombre: 'Mateo', apellido: 'Ríos', fechaNacimiento: '2014-08-03', email: 'familia.rios@mail.com', telefono: '351-555-0102', estadoCuota: 'AL_DIA' },
  // Al día, con 3 actividades (en el máximo). DNI de 7 dígitos. 68 años.
  { id: 3, dni: '9876543', nombre: 'Marta', apellido: 'Gómez', fechaNacimiento: '1958-02-20', email: 'marta.gomez@mail.com', telefono: '351-555-0103', estadoCuota: 'AL_DIA' },
  // Moroso, con 1 actividad: puede verla y darse de baja, pero no inscribirse (RN4). 42 años.
  { id: 4, dni: '30111222', nombre: 'Jorge', apellido: 'Paz', fechaNacimiento: '1983-11-30', email: 'jorge.paz@mail.com', telefono: '351-555-0104', estadoCuota: 'MOROSO' },
];

// Inscripciones como estarían en la tabla de la base: guardan los ids, no los objetos.
export const inscripcionesIniciales = [
  { id: 1, socioId: 1, actividadId: 2, fechaInscripcion: '2026-03-02', estado: 'BAJA' },
  { id: 2, socioId: 2, actividadId: 1, fechaInscripcion: '2026-03-05', estado: 'ACTIVA' },
  { id: 3, socioId: 2, actividadId: 6, fechaInscripcion: '2026-04-11', estado: 'ACTIVA' },
  { id: 4, socioId: 3, actividadId: 4, fechaInscripcion: '2026-02-20', estado: 'ACTIVA' },
  { id: 5, socioId: 3, actividadId: 6, fechaInscripcion: '2026-02-20', estado: 'ACTIVA' },
  { id: 6, socioId: 3, actividadId: 8, fechaInscripcion: '2026-03-01', estado: 'ACTIVA' },
  { id: 7, socioId: 4, actividadId: 7, fechaInscripcion: '2026-05-15', estado: 'ACTIVA' },
];
