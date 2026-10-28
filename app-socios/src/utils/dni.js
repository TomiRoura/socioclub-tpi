// RN7: el DNI tiene 7 u 8 dígitos numéricos, sin puntos ni espacios.
// ^ y $ obligan a que TODO el texto cumpla el patrón (sin ellos, "123456789" pasaría por contener 8 dígitos).
export function esDniValido(dni) {
  return /^\d{7,8}$/.test(String(dni));
}
