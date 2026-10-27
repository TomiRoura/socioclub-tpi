import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useSocio } from '../contexto/SocioContext';
import { inscribir, obtenerActividad } from '../servicios/api';
import { formatearPesos } from '../utils/formato';
import Mensaje from './Mensaje';

function DetalleActividad() {
  const { id } = useParams(); // el :id de la ruta /actividades/:id (llega como texto)
  const { socio } = useSocio();
  const [actividad, setActividad] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [mensaje, setMensaje] = useState(null);

  // Depende de "id": si se navega de un detalle a otro, se vuelve a pedir la actividad.
  useEffect(() => {
    let vigente = true;
    setCargando(true);
    setError('');

    obtenerActividad(id)
      .then((datos) => {
        if (vigente) setActividad(datos);
      })
      .catch((err) => {
        if (vigente) setError(err.status === 404 ? 'Esa actividad no existe.' : err.message);
      })
      .finally(() => {
        if (vigente) setCargando(false);
      });

    return () => {
      vigente = false;
    };
  }, [id]);

  async function manejarInscripcion() {
    setMensaje(null);
    try {
      const inscripcion = await inscribir(socio.id, actividad.id);
      setActividad(inscripcion.actividad); // trae el cupo ya descontado
      setMensaje({ tipo: 'exito', texto: `Listo, quedaste inscripto en ${actividad.nombre}.` });
    } catch (err) {
      setMensaje({ tipo: 'error', texto: err.message, regla: err.regla });
    }
  }

  if (cargando) {
    return <p className="estado">Cargando actividad…</p>;
  }

  if (error) {
    return (
      <section>
        <p className="estado estado--error">{error}</p>
        <Link to="/actividades">Volver a actividades</Link>
      </section>
    );
  }

  const sinCupo = actividad.cupoDisponible === 0;

  return (
    <section className={sinCupo ? 'detalle detalle--sin-cupo' : 'detalle'}>
      <Link className="detalle__volver" to="/actividades">
        ← Volver a actividades
      </Link>
      <span className="detalle__disciplina">{actividad.disciplina}</span>
      <h1>{actividad.nombre}</h1>

      <dl className="detalle__datos">
        <dt>Profesor/a</dt>
        <dd>{actividad.profesor}</dd>
        <dt>Días y horario</dt>
        <dd>
          {actividad.dias} · {actividad.horario}
        </dd>
        <dt>Edades</dt>
        <dd>
          De {actividad.edadMinima} a {actividad.edadMaxima} años
        </dd>
        <dt>Cupo</dt>
        <dd>
          {sinCupo ? (
            <span className="etiqueta etiqueta--sin-cupo">Sin cupo</span>
          ) : (
            `${actividad.cupoDisponible} lugares libres de ${actividad.cupoMaximo}`
          )}
        </dd>
        <dt>Cuota mensual</dt>
        <dd>{formatearPesos(actividad.cuotaMensual)}</dd>
      </dl>

      {mensaje && <Mensaje tipo={mensaje.tipo} texto={mensaje.texto} regla={mensaje.regla} />}

      <button type="button" className="boton" disabled={sinCupo} onClick={manejarInscripcion}>
        Inscribirme
      </button>
    </section>
  );
}

export default DetalleActividad;
