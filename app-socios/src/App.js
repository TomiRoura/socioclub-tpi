import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { SocioProvider } from './contexto/SocioContext';
import RutaPrivada from './componentes/RutaPrivada';
import Encabezado from './componentes/Encabezado';
import Pie from './componentes/Pie';
import Ingreso from './componentes/Ingreso';
import ListaActividades from './componentes/ListaActividades';
import DetalleActividad from './componentes/DetalleActividad';
import MisActividades from './componentes/MisActividades';

function App() {
  return (
    // El provider envuelve todo: cualquier componente de adentro puede leer el socio con useSocio().
    <SocioProvider>
      <BrowserRouter>
        <Encabezado />
        <main className="contenido">
          <Routes>
            <Route path="/" element={<Ingreso />} />
            <Route
              path="/actividades"
              element={
                <RutaPrivada>
                  <ListaActividades />
                </RutaPrivada>
              }
            />
            <Route
              path="/actividades/:id"
              element={
                <RutaPrivada>
                  <DetalleActividad />
                </RutaPrivada>
              }
            />
            <Route
              path="/mis-actividades"
              element={
                <RutaPrivada>
                  <MisActividades />
                </RutaPrivada>
              }
            />
            {/* Cualquier otra dirección vuelve al ingreso */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
        <Pie />
      </BrowserRouter>
    </SocioProvider>
  );
}

export default App;
