// Reglas del negocio en un solo lugar: si cambia una regla, se cambia acá y en ningún otro archivo.

// RN5: máximo de actividades activas por socio.
export const MAX_ACTIVIDADES = 3;

// RN6: descuento sobre la suma de cuotas según la cantidad de actividades.
// IMPORTANTE: ordenado de mayor a menor; se aplica el primer escalón que se cumple
// (con 3 o más actividades, 15 %; con 2, 10 %; con 1 o ninguna, sin descuento).
export const DESCUENTOS = [
  { desdeActividades: 3, porcentaje: 15 },
  { desdeActividades: 2, porcentaje: 10 },
];

// Opciones del filtro del catálogo. Tienen que coincidir con el campo "disciplina" de la API.
export const DISCIPLINAS = [
  'Fútbol',
  'Vóley',
  'Básquet',
  'Natación',
  'Patín',
  'Ajedrez',
  'Fitness',
  'Bienestar',
];
