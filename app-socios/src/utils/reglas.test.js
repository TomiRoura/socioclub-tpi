import { calcularEdad, validarInscripcion } from './reglas';
import { MAX_ACTIVIDADES } from './constantes';

// Fecha fija para que los tests den lo mismo cualquier día: 26 de octubre de 2026 (los meses van de 0 a 11).
const HOY = new Date(2026, 9, 26);

describe('calcularEdad', () => {
  test('si el cumpleaños es mañana, todavía no cumplió', () => {
    expect(calcularEdad('2014-10-27', HOY)).toBe(11);
  });

  test('si el cumpleaños es hoy, ya cumplió', () => {
    expect(calcularEdad('2014-10-26', HOY)).toBe(12);
  });

  test('si el cumpleaños fue en un mes anterior, ya cumplió', () => {
    expect(calcularEdad('2014-03-10', HOY)).toBe(12);
  });

  test('si el cumpleaños es en un mes posterior, todavía no cumplió', () => {
    expect(calcularEdad('2014-12-01', HOY)).toBe(11);
  });
});

describe('validarInscripcion (RN1 a RN5)', () => {
  const socio = { id: 1, estadoCuota: 'AL_DIA', fechaNacimiento: '1997-05-14' }; // 29 años
  const funcional = { id: 7, edadMinima: 18, edadMaxima: 65, cupoDisponible: 5 };

  test('si cumple todo, devuelve null', () => {
    expect(validarInscripcion(socio, funcional, [], HOY)).toBeNull();
  });

  test('RN4: un socio moroso no puede inscribirse', () => {
    const moroso = { ...socio, estadoCuota: 'MOROSO' };
    expect(validarInscripcion(moroso, funcional, [], HOY)).toEqual({
      regla: 'RN4',
      error: 'Debe regularizar su cuota en secretaría',
    });
  });

  test('RN2: no puede inscribirse dos veces en la misma actividad', () => {
    const resultado = validarInscripcion(socio, funcional, [{ actividadId: 7 }], HOY);
    expect(resultado.regla).toBe('RN2');
  });

  test('RN5: no puede superar el máximo de actividades', () => {
    // Se arma con MAX_ACTIVIDADES para que el test siga valiendo si el máximo cambia.
    const activas = Array.from({ length: MAX_ACTIVIDADES }, (_, i) => ({ actividadId: 100 + i }));
    expect(validarInscripcion(socio, funcional, activas, HOY)).toEqual({
      regla: 'RN5',
      error: `Alcanzó el máximo de ${MAX_ACTIVIDADES} actividades`,
    });
  });

  test('RN3: la edad tiene que estar dentro del rango y el mensaje lo indica', () => {
    const chico = { ...socio, fechaNacimiento: '2014-10-27' }; // 11 años
    expect(validarInscripcion(chico, funcional, [], HOY)).toEqual({
      regla: 'RN3',
      error: 'La edad permitida para esta actividad es de 18 a 65 años',
    });
  });

  test('RN1: sin cupo disponible no se puede inscribir', () => {
    const llena = { ...funcional, cupoDisponible: 0 };
    expect(validarInscripcion(socio, llena, [], HOY).regla).toBe('RN1');
  });

  test('respeta el orden RN4 -> RN2 -> RN5 -> RN3 -> RN1', () => {
    const chico = { ...socio, fechaNacimiento: '2014-10-27' };
    const llena = { ...funcional, cupoDisponible: 0 };
    const activas = Array.from({ length: MAX_ACTIVIDADES }, (_, i) => ({ actividadId: 7 + i }));

    // Falla todo: gana RN4.
    expect(validarInscripcion({ ...chico, estadoCuota: 'MOROSO' }, llena, activas, HOY).regla).toBe('RN4');
    // Duplicado y en el máximo: gana RN2.
    expect(validarInscripcion(chico, llena, activas, HOY).regla).toBe('RN2');
    // En el máximo, edad fuera de rango y sin cupo: gana RN5.
    expect(validarInscripcion(chico, llena, [{ actividadId: 1 }, ...activas.slice(1)], HOY).regla).toBe('RN5');
    // Edad fuera de rango y sin cupo: gana RN3.
    expect(validarInscripcion(chico, llena, [], HOY).regla).toBe('RN3');
  });
});
