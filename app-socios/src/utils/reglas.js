import { MAX_ACTIVIDADES } from './constantes';

// Edad en años cumplidos a la fecha "hoy" (por defecto, el día actual).
// fechaNacimiento viene como texto "AAAA-MM-DD". Se separa a mano porque new Date("2014-10-27")
// lo interpreta en UTC y en Argentina (UTC-3) cae en el día anterior.
export function calcularEdad(fechaNacimiento, hoy = new Date()) {
  const [anio, mes, dia] = fechaNacimiento.split('-').map(Number);
  const mesActual = hoy.getMonth() + 1; // getMonth() va de 0 a 11
  let edad = hoy.getFullYear() - anio;
  // Si este año todavía no llegó el cumpleaños, tiene un año menos.
  const yaCumplio = mesActual > mes || (mesActual === mes && hoy.getDate() >= dia);
  if (!yaCumplio) {
    edad = edad - 1;
  }
  return edad;
}

// Valida las reglas RN1 a RN5 para inscribir a un socio en una actividad.
// Devuelve null si se puede inscribir, o { regla, error } con la PRIMERA regla que falla.
// Orden fijo, el mismo que tiene que usar el backend: RN4 -> RN2 -> RN5 -> RN3 -> RN1.
// La usa el modo de datos locales (servicios/apiLocal.js) para simular los 409 de la API.
export function validarInscripcion(socio, actividad, inscripcionesActivas, hoy = new Date()) {
  if (socio.estadoCuota === 'MOROSO') {
    return { regla: 'RN4', error: 'Debe regularizar su cuota en secretaría' };
  }
  if (inscripcionesActivas.some((inscripcion) => inscripcion.actividadId === actividad.id)) {
    return { regla: 'RN2', error: 'El socio ya está inscripto en esta actividad' };
  }
  if (inscripcionesActivas.length >= MAX_ACTIVIDADES) {
    return { regla: 'RN5', error: `Alcanzó el máximo de ${MAX_ACTIVIDADES} actividades` };
  }
  const edad = calcularEdad(socio.fechaNacimiento, hoy);
  if (edad < actividad.edadMinima || edad > actividad.edadMaxima) {
    return {
      regla: 'RN3',
      error: `La edad permitida para esta actividad es de ${actividad.edadMinima} a ${actividad.edadMaxima} años`,
    };
  }
  if (actividad.cupoDisponible <= 0) {
    return { regla: 'RN1', error: 'La actividad no tiene cupo disponible' };
  }
  return null;
}
