import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import { formatearPesos } from '../utils/formato';
import './TarjetaActividad.css';

// Componente de presentación: solo muestra la actividad que recibe por props.
// No llama a la API: cuando se hace clic en "Inscribirme" le avisa al padre con onInscribir.
function TarjetaActividad({ actividad, onInscribir }) {
  const sinCupo = actividad.cupoDisponible === 0;

  return (
    // Clase dinámica: sin cupo, la tarjeta se ve en gris (ver TarjetaActividad.css).
    <article className={sinCupo ? 'tarjeta tarjeta--sin-cupo' : 'tarjeta'}>
      <span className="tarjeta__disciplina">{actividad.disciplina}</span>
      <h2 className="tarjeta__nombre">
        <Link to={`/actividades/${actividad.id}`}>{actividad.nombre}</Link>
      </h2>
      <p className="tarjeta__horario">
        {actividad.dias} · {actividad.horario}
      </p>
      <p className="tarjeta__cuota">{formatearPesos(actividad.cuotaMensual)} por mes</p>

      {sinCupo ? (
        <span className="etiqueta etiqueta--sin-cupo">Sin cupo</span>
      ) : (
        <span className="etiqueta">Cupo disponible: {actividad.cupoDisponible}</span>
      )}

      <div className="tarjeta__acciones">
        <Link className="boton boton--secundario" to={`/actividades/${actividad.id}`}>
          Ver detalle
        </Link>
        <button
          type="button"
          className="boton"
          disabled={sinCupo}
          onClick={() => onInscribir(actividad)}
        >
          Inscribirme
        </button>
      </div>
    </article>
  );
}

TarjetaActividad.propTypes = {
  actividad: PropTypes.shape({
    id: PropTypes.number.isRequired,
    nombre: PropTypes.string.isRequired,
    disciplina: PropTypes.string.isRequired,
    dias: PropTypes.string.isRequired,
    horario: PropTypes.string.isRequired,
    cupoDisponible: PropTypes.number.isRequired,
    cuotaMensual: PropTypes.number.isRequired,
  }).isRequired,
  onInscribir: PropTypes.func.isRequired,
};

export default TarjetaActividad;
