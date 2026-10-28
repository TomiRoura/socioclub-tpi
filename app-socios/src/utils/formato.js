// 18000 -> "$18.000"
export function formatearPesos(monto) {
  return '$' + monto.toLocaleString('es-AR');
}

// "2026-10-26" -> "26/10/2026"
export function formatearFecha(fechaTexto) {
  return fechaTexto.split('-').reverse().join('/');
}
