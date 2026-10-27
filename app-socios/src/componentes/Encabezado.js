import { NavLink, useNavigate } from 'react-router-dom';
import { useSocio } from '../contexto/SocioContext';

function Encabezado() {
  const { socio, salir } = useSocio();
  const navigate = useNavigate();

  function manejarSalir() {
    salir();
    navigate('/');
  }

  return (
    <header className="encabezado">
      <div className="encabezado__barra">
        <span className="encabezado__marca">Mi Club · Los Algarrobos</span>

        {/* El menú solo se muestra si hay un socio que ingresó */}
        {socio && (
          <nav className="encabezado__nav">
            <NavLink to="/actividades">Actividades</NavLink>
            <NavLink to="/mis-actividades">Mis actividades</NavLink>
            <span className="encabezado__socio">
              {socio.nombre} {socio.apellido}
            </span>
            <button type="button" className="boton boton--secundario" onClick={manejarSalir}>
              Salir
            </button>
          </nav>
        )}
      </div>

      {/* RN4: un moroso puede consultar y darse de baja, pero no inscribirse. Se le avisa desde el principio. */}
      {socio && socio.estadoCuota === 'MOROSO' && (
        <p className="encabezado__aviso">
          Tu cuota no está al día: podés ver tus actividades y darte de baja, pero para inscribirte
          tenés que regularizarla en secretaría.
        </p>
      )}
    </header>
  );
}

export default Encabezado;
