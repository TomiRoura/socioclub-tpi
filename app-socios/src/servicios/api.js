// Único punto de la app que habla con el backend. Los componentes importan estas funciones
// y no saben si los datos vienen de la API (Axios) o de los datos locales (apiLocal.js).
import axios from 'axios';
import * as local from './apiLocal';

// Modo local por defecto: solo se usa la API si la variable vale exactamente "false".
// (Las variables de .env se leen al hacer npm start: si se cambian, hay que reiniciarlo.)
const usarDatosLocales = process.env.REACT_APP_USAR_DATOS_LOCALES !== 'false';

// Instancia de Axios con la dirección base: cada función solo escribe el final de la ruta.
const cliente = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
});

function crearError(mensaje, status, regla) {
  const error = new Error(mensaje);
  error.status = status;
  error.regla = regla;
  return error;
}

// Deja todos los errores con la misma forma, venga de donde venga:
// error.message (texto para el socio), error.status (404, 409...) y error.regla ("RN1"...).
function normalizarError(error) {
  if (!error.response) {
    // No hubo respuesta: el backend está apagado, no hay red o lo bloqueó CORS.
    return crearError('No se pudo conectar con el servidor. Probá de nuevo en un rato.', 0);
  }
  const { status, data } = error.response;
  const mensaje = (data && data.error) || 'Ocurrió un error inesperado. Probá de nuevo.';
  return crearError(mensaje, status, data && data.regla);
}

// Ejecuta el pedido según el modo y devuelve solo los datos (en Axios vienen en respuesta.data).
async function pedir(pedidoApi, pedidoLocal) {
  try {
    if (usarDatosLocales) {
      return await pedidoLocal();
    }
    const respuesta = await pedidoApi();
    return respuesta.data;
  } catch (error) {
    throw normalizarError(error);
  }
}

// GET /actividades?disciplina=... (sin disciplina, trae todas)
export function listarActividades(disciplina) {
  return pedir(
    // Axios no manda los parámetros que valen undefined
    () => cliente.get('/actividades', { params: { disciplina: disciplina || undefined } }),
    () => local.listarActividades(disciplina)
  );
}

// GET /actividades/{id}
export function obtenerActividad(id) {
  return pedir(
    () => cliente.get(`/actividades/${id}`),
    () => local.obtenerActividad(id)
  );
}

// GET /socios/dni/{dni}
export function buscarSocioPorDni(dni) {
  return pedir(
    () => cliente.get(`/socios/dni/${dni}`),
    () => local.buscarSocioPorDni(dni)
  );
}

// GET /socios/{id}/inscripciones
export function listarInscripcionesActivas(socioId) {
  return pedir(
    () => cliente.get(`/socios/${socioId}/inscripciones`),
    () => local.listarInscripcionesActivas(socioId)
  );
}

// POST /inscripciones con { socioId, actividadId }
export function inscribir(socioId, actividadId) {
  return pedir(
    () => cliente.post('/inscripciones', { socioId, actividadId }),
    () => local.inscribir(socioId, actividadId)
  );
}

// PUT /inscripciones/{id}/baja
export function darDeBaja(inscripcionId) {
  return pedir(
    () => cliente.put(`/inscripciones/${inscripcionId}/baja`),
    () => local.darDeBaja(inscripcionId)
  );
}
