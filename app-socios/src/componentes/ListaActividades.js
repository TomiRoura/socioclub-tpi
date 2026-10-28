import { useEffect, useState } from 'react';
import { useSocio } from '../contexto/SocioContext';
import { inscribir, listarActividades } from '../servicios/api';
import { DISCIPLINAS } from '../utils/constantes';
import TarjetaActividad from './TarjetaActividad';
import Mensaje from './Mensaje';
import './ListaActividades.css';

function ListaActividades() {
  const { socio } = useSocio();
  const [actividades, setActividades] = useState([]);
  const [disciplina, setDisciplina] = useState(''); // '' = todas
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [mensaje, setMensaje] = useState(null);

  // Se ejecuta al montar el componente y cada vez que cambia "disciplina" (por eso está en el arreglo).
  useEffect(() => {
    // Si el socio cambia el filtro rápido, puede llegar tarde la respuesta del filtro anterior.
    // "vigente" pasa a false en la limpieza, y así esa respuesta vieja se ignora.
    let vigente = true;
    setCargando(true);
    setError('');

    listarActividades(disciplina)
      .then((datos) => {
        if (vigente) setActividades(datos);
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
  }, [disciplina]);

  async function manejarInscripcion(actividad) {
    setMensaje(null);
    try {
      const inscripcion = await inscribir(socio.id, actividad.id);
      // La respuesta trae la actividad con el cupo actualizado: se reemplaza en la lista.
      // map devuelve un arreglo NUEVO; React detecta el cambio y vuelve a dibujar las tarjetas.
      setActividades((actuales) =>
        actuales.map((a) => (a.id === inscripcion.actividad.id ? inscripcion.actividad : a))
      );
      setMensaje({ tipo: 'exito', texto: `Listo, quedaste inscripto en ${actividad.nombre}.` });
    } catch (err) {
      // Los 409 traen el texto de la regla que falló, por ejemplo "La actividad no tiene cupo disponible".
      setMensaje({ tipo: 'error', texto: err.message, regla: err.regla });
    }
  }

  return (
    <section>
      <div className="lista__cabecera">
        <h1>Actividades</h1>
        <label className="lista__filtro">
          Disciplina
          <select value={disciplina} onChange={(evento) => setDisciplina(evento.target.value)}>
            <option value="">Todas</option>
            {DISCIPLINAS.map((nombre) => (
              <option key={nombre} value={nombre}>
                {nombre}
              </option>
            ))}
          </select>
        </label>
      </div>

      {mensaje && <Mensaje tipo={mensaje.tipo} texto={mensaje.texto} regla={mensaje.regla} />}

      {cargando && <p className="estado">Cargando actividades…</p>}
      {!cargando && error && <p className="estado estado--error">{error}</p>}
      {!cargando && !error && actividades.length === 0 && (
        <p className="estado">No hay actividades para esa disciplina.</p>
      )}

      {!cargando && !error && (
        <div className="lista__grilla">
          {/* key: el id identifica a cada tarjeta para que React sepa cuál cambió */}
          {actividades.map((actividad) => (
            <TarjetaActividad
              key={actividad.id}
              actividad={actividad}
              onInscribir={manejarInscripcion}
            />
          ))}
        </div>
      )}
    </section>
  );
}

export default ListaActividades;
