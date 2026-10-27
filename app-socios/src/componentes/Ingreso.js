import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { useSocio } from '../contexto/SocioContext';
import { buscarSocioPorDni } from '../servicios/api';
import { esDniValido } from '../utils/dni';

function Ingreso() {
  const [dni, setDni] = useState('');
  const [error, setError] = useState('');
  const [buscando, setBuscando] = useState(false);
  const { socio, ingresar } = useSocio();
  const navigate = useNavigate();

  // Si ya ingresó, no tiene sentido mostrarle el formulario otra vez.
  if (socio) {
    return <Navigate to="/actividades" replace />;
  }

  async function manejarEnvio(evento) {
    evento.preventDefault(); // evita que el formulario recargue la página
    const dniIngresado = dni.trim();

    // RN7: se valida antes de llamar a la API; el error se muestra junto al campo.
    if (!esDniValido(dniIngresado)) {
      setError('El DNI tiene que tener 7 u 8 números, sin puntos.');
      return;
    }

    setError('');
    setBuscando(true);
    try {
      const socioEncontrado = await buscarSocioPorDni(dniIngresado);
      ingresar(socioEncontrado); // queda guardado en el contexto
      navigate('/actividades');
    } catch (err) {
      setError(err.status === 404 ? 'No encontramos un socio con ese DNI.' : err.message);
      setBuscando(false);
    }
  }

  return (
    <section className="ingreso">
      <h1>Ingresá a Mi Club</h1>
      <p>Con tu DNI podés ver las actividades, inscribirte y consultar tu cuota.</p>

      <form className="ingreso__formulario" onSubmit={manejarEnvio} noValidate>
        <label htmlFor="dni">DNI</label>
        <input
          id="dni"
          type="text"
          inputMode="numeric"
          placeholder="Sin puntos, por ejemplo 40123456"
          value={dni}
          onChange={(evento) => setDni(evento.target.value)}
          aria-invalid={error !== ''}
          aria-describedby="dni-error"
        />
        {error && (
          <p id="dni-error" className="ingreso__error" role="alert">
            {error}
          </p>
        )}
        <button type="submit" className="boton" disabled={buscando}>
          {buscando ? 'Buscando…' : 'Ingresar'}
        </button>
      </form>
    </section>
  );
}

export default Ingreso;
