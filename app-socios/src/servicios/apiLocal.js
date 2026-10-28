// Simula el backend en memoria para trabajar sin la API (hito de la semana 2).
// Devuelve los mismos datos y los mismos errores que el contrato (CONTRATO-API.md).
// Los componentes NO importan este archivo: solo lo usa servicios/api.js.
import { actividadesIniciales, sociosIniciales, inscripcionesIniciales } from '../datos/datosLocales';
import { validarInscripcion } from '../utils/reglas';

// "Base de datos" en memoria. Se copian los datos iniciales porque se van a modificar
// (cupo e inscripciones). Si se recarga la página, todo vuelve al estado inicial.
const actividades = actividadesIniciales.map((actividad) => ({ ...actividad }));
const socios = sociosIniciales;
const inscripciones = inscripcionesIniciales.map((inscripcion) => ({ ...inscripcion }));

// Demora para que se vea el estado "Cargando…", como con un servidor real.
const DEMORA_MS = 300;

function esperar() {
  return new Promise((resolve) => setTimeout(resolve, DEMORA_MS));
}

// Copia profunda de lo que se devuelve: así los componentes nunca modifican la "base de datos"
// por accidente, igual que pasa con un JSON que llega por la red.
function copiar(dato) {
  return JSON.parse(JSON.stringify(dato));
}

// Arma un error con la misma forma que el que tira Axios: error.response.status y error.response.data.
// Así api.js trata igual los errores de los dos modos.
function errorHttp(status, mensaje, regla) {
  const error = new Error(mensaje);
  error.response = { status, data: regla ? { error: mensaje, regla } : { error: mensaje } };
  return error;
}

// Los ids de la URL llegan como texto ("3"): se convierten a número para comparar.
function buscarActividad(id) {
  return actividades.find((actividad) => actividad.id === Number(id));
}

function buscarSocio(id) {
  return socios.find((socio) => socio.id === Number(id));
}

function activasDelSocio(socioId) {
  return inscripciones.filter((i) => i.socioId === Number(socioId) && i.estado === 'ACTIVA');
}

// Arma la inscripción como la devuelve la API: con la actividad adentro y SIN el socio
// (si la inscripción trajera al socio y el socio sus inscripciones, el JSON sería infinito).
function comoRespuesta(inscripcion) {
  const { socioId, actividadId, ...resto } = inscripcion;
  return { ...resto, actividad: buscarActividad(actividadId) };
}

// Fecha de hoy como "AAAA-MM-DD" en hora local (toISOString usa UTC y después de las 21 h daría mañana).
function hoyComoTexto() {
  const hoy = new Date();
  const mes = String(hoy.getMonth() + 1).padStart(2, '0');
  const dia = String(hoy.getDate()).padStart(2, '0');
  return `${hoy.getFullYear()}-${mes}-${dia}`;
}

// GET /api/actividades?disciplina=...
export async function listarActividades(disciplina) {
  await esperar();
  const resultado = disciplina
    ? actividades.filter((actividad) => actividad.disciplina === disciplina)
    : actividades;
  return copiar(resultado);
}

// GET /api/actividades/{id}
export async function obtenerActividad(id) {
  await esperar();
  const actividad = buscarActividad(id);
  if (!actividad) {
    throw errorHttp(404, 'Actividad no encontrada');
  }
  return copiar(actividad);
}

// GET /api/socios/dni/{dni}
export async function buscarSocioPorDni(dni) {
  await esperar();
  const socio = socios.find((s) => s.dni === dni);
  if (!socio) {
    throw errorHttp(404, 'Socio no encontrado');
  }
  return copiar(socio);
}

// GET /api/socios/{id}/inscripciones (solo las activas)
export async function listarInscripcionesActivas(socioId) {
  await esperar();
  if (!buscarSocio(socioId)) {
    throw errorHttp(404, 'Socio no encontrado');
  }
  return copiar(activasDelSocio(socioId).map(comoRespuesta));
}

// POST /api/inscripciones con { socioId, actividadId } -> 201 / 404 / 409
export async function inscribir(socioId, actividadId) {
  await esperar();
  const socio = buscarSocio(socioId);
  const actividad = buscarActividad(actividadId);
  if (!socio) {
    throw errorHttp(404, 'Socio no encontrado');
  }
  if (!actividad) {
    throw errorHttp(404, 'Actividad no encontrada');
  }

  const fallo = validarInscripcion(socio, actividad, activasDelSocio(socio.id));
  if (fallo) {
    throw errorHttp(409, fallo.error, fallo.regla);
  }

  actividad.cupoDisponible = actividad.cupoDisponible - 1;
  const nueva = {
    id: Math.max(...inscripciones.map((i) => i.id)) + 1,
    socioId: socio.id,
    actividadId: actividad.id,
    fechaInscripcion: hoyComoTexto(),
    estado: 'ACTIVA',
  };
  inscripciones.push(nueva);
  return copiar(comoRespuesta(nueva));
}

// PUT /api/inscripciones/{id}/baja -> 200 / 404
export async function darDeBaja(inscripcionId) {
  await esperar();
  const inscripcion = inscripciones.find(
    (i) => i.id === Number(inscripcionId) && i.estado === 'ACTIVA'
  );
  if (!inscripcion) {
    throw errorHttp(404, 'Inscripción no encontrada');
  }
  // Baja lógica: la inscripción queda guardada con estado BAJA (no se borra) y se libera el cupo.
  inscripcion.estado = 'BAJA';
  const actividad = buscarActividad(inscripcion.actividadId);
  actividad.cupoDisponible = actividad.cupoDisponible + 1;
  return copiar(comoRespuesta(inscripcion));
}
