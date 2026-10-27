import { Navigate } from 'react-router-dom';
import { useSocio } from '../contexto/SocioContext';

// Envuelve una pantalla que solo puede ver un socio que ingresó.
// Si no hay socio, redirige al ingreso. "replace" reemplaza la entrada del historial,
// así el botón "Atrás" del navegador no vuelve a la pantalla privada.
function RutaPrivada({ children }) {
  const { socio } = useSocio();

  if (!socio) {
    return <Navigate to="/" replace />;
  }
  return children;
}

export default RutaPrivada;
