import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useSocio } from '../contexto/SocioContext';
import { darDeBaja, listarInscripcionesActivas } from '../servicios/api';
import { formatearFecha, formatearPesos } from '../utils/formato';
import Mensaje from './Mensaje';
import ResumenCuota from './ResumenCuota';

function MisActividades() {
  const { socio } = useSocio();
  const [inscripciones, setInscripciones] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [mensaje, setMensaje] = useState(null);

  // Depende de socio.id: si cambia el socio, se vuelven a pedir sus inscripciones.
  useEffect(() => {
    let vigente = true;

    listarInscripcionesActivas(socio.id)
      .then((datos) => {
        if (vigente) setInscripciones(datos);
      })
      .catch((err) => {
        if (vigente) setError(err.message);
      })
      .finally(() => {
        if (vigente) setCargando(false);
      });

    return () => {
      vigente = false;
    };
  }, [socio.id]);

  async function manejarBaja(inscripcion) {
    setMensaje(null);
    try {
      await darDeBaja(inscripcion.id);
      // Ya no está activa: se saca de la lista. filter devuelve un arreglo nuevo y React vuelve a dibujar
      // la lista y el resumen de la cuota.
      setInscripciones((actuales) => actuales.filter((i) => i.id !== inscripcion.id));
      setMensaje({ tipo: 'exito', texto: `Te diste de baja de ${inscripcion.actividad.nombre}.` });
    } catch (err) {
      setMensaje({ tipo: 'error', texto: err.message, regla: err.regla });
    }
  }

  // El resumen de la cuota solo necesita las actividades, no las inscripciones.
  const actividades = inscripciones.map((inscripcion) => inscripcion.actividad);

  return (
    <section>
      <h1>Mis actividades</h1>

      {mensaje && <Mensaje tipo={mensaje.tipo} texto={mensaje.texto} regla={mensaje.regla} />}

      {cargando && <p className="estado">Cargando tus actividades…</p>}
      {!cargando && error && <p className="estado estado--error">{error}</p>}

      {!cargando && !error && inscripciones.length === 0 && (
        <p className="estado">
          Todavía no estás inscripto en ninguna actividad.{' '}
          <Link to="/actividades">Ver actividades</Link>
        </p>
      )}

      {!cargando && !error && inscripciones.length > 0 && (
        <div className="mis-actividades">
          <ul className="mis-actividades__lista">
            {inscripciones.map((inscripcion) => (
              <li key={inscripcion.id} className="inscripcion">
                <div className="inscripcion__datos">
                  <strong>{inscripcion.actividad.nombre}</strong>
                  <span>
                    {inscripcion.actividad.dias} · {inscripcion.actividad.horario}
                  </span>
                  <span className="inscripcion__fecha">
                    Inscripto desde el {formatearFecha(inscripcion.fechaInscripcion)}
                  </span>
                </div>
                <span className="inscripcion__cuota">
                  {formatearPesos(inscripcion.actividad.cuotaMensual)}
                </span>
                <button
                  type="button"
                  className="boton boton--peligro"
                  onClick={() => manejarBaja(inscripcion)}
                >
                  Darme de baja
                </button>
              </li>
            ))}
          </ul>
          <ResumenCuota actividades={actividades} />
        </div>
      )}
    </section>
  );
}

export default MisActividades;
