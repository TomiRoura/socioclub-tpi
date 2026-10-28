import { calcularCuotaMensual } from './cuota';

const futbol = { nombre: 'Fútbol infantil', cuotaMensual: 18000 };
const voley = { nombre: 'Vóley mixto', cuotaMensual: 20000 };
const basquet = { nombre: 'Básquet juvenil', cuotaMensual: 19000 };

describe('calcularCuotaMensual (RN6)', () => {
  test('sin actividades la cuota es 0', () => {
    expect(calcularCuotaMensual([])).toBe(0);
  });

  test('con 1 actividad no hay descuento', () => {
    expect(calcularCuotaMensual([futbol])).toBe(18000);
  });

  test('con 2 actividades aplica 10 % de descuento', () => {
    // (18000 + 20000) * 0,90 = 34200
    expect(calcularCuotaMensual([futbol, voley])).toBe(34200);
  });

  test('con 3 actividades aplica 15 % de descuento', () => {
    // (18000 + 20000 + 19000) * 0,85 = 48450
    expect(calcularCuotaMensual([futbol, voley, basquet])).toBe(48450);
  });

  test('redondea al peso entero más cercano', () => {
    // (18333 + 10000) * 0,90 = 25499,7 -> 25500
    expect(calcularCuotaMensual([{ cuotaMensual: 18333 }, { cuotaMensual: 10000 }])).toBe(25500);
  });
});
