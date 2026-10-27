import { DESCUENTOS } from './constantes';

// Suma las cuotas mensuales de una lista de actividades, sin descuento.
export function sumarCuotas(actividades) {
  return actividades.reduce((total, actividad) => total + actividad.cuotaMensual, 0);
}

// Devuelve el porcentaje de descuento que corresponde a una cantidad de actividades (0 si no hay).
export function obtenerPorcentajeDescuento(cantidad) {
  const escalon = DESCUENTOS.find((descuento) => cantidad >= descuento.desdeActividades);
  return escalon ? escalon.porcentaje : 0;
}

// RN6: cuota mensual estimada = suma de cuotas menos el descuento por cantidad.
// Redondeo: Math.round al peso entero más cercano (desde 0,50 sube), porque no se cobran centavos.
// Primero se multiplica y después se divide para no arrastrar errores de decimales (0.85 no es exacto en binario).
export function calcularCuotaMensual(actividades) {
  const subtotal = sumarCuotas(actividades);
  const porcentaje = obtenerPorcentajeDescuento(actividades.length);
  return Math.round((subtotal * (100 - porcentaje)) / 100);
}
