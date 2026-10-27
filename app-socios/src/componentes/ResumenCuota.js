import PropTypes from 'prop-types';
import { calcularCuotaMensual, obtenerPorcentajeDescuento, sumarCuotas } from '../utils/cuota';
import { formatearPesos } from '../utils/formato';
import './ResumenCuota.css';

// RN6: muestra la cuota mensual estimada de las actividades que recibe.
// No tiene estado: todo se calcula a partir de las props en cada render.
function ResumenCuota({ actividades }) {
  const subtotal = sumarCuotas(actividades);
  const porcentaje = obtenerPorcentajeDescuento(actividades.length);
  const total = calcularCuotaMensual(actividades);

  return (
    <aside className="resumen-cuota">
      <h2>Cuota mensual estimada</h2>
      <dl>
        <div className="resumen-cuota__fila">
          <dt>
            Subtotal ({actividades.length} {actividades.length === 1 ? 'actividad' : 'actividades'})
          </dt>
          <dd>{formatearPesos(subtotal)}</dd>
        </div>
        {porcentaje > 0 && (
          <div className="resumen-cuota__fila resumen-cuota__fila--descuento">
            <dt>Descuento del {porcentaje} %</dt>
            <dd>-{formatearPesos(subtotal - total)}</dd>
          </div>
        )}
        <div className="resumen-cuota__fila resumen-cuota__fila--total">
          <dt>Total por mes</dt>
          <dd>{formatearPesos(total)}</dd>
        </div>
      </dl>
    </aside>
  );
}

ResumenCuota.propTypes = {
  actividades: PropTypes.arrayOf(
    PropTypes.shape({
      cuotaMensual: PropTypes.number.isRequired,
    })
  ).isRequired,
};

export default ResumenCuota;
