import { createContext, useContext, useState } from 'react';

// Contexto con el socio que ingresó. Lo necesitan varias pantallas (encabezado, rutas privadas,
// inscribirse, mis actividades), así que en vez de pasarlo por props de pantalla en pantalla
// se guarda acá y cada componente lo lee con useSocio().
const SocioContext = createContext(null);

export function SocioProvider({ children }) {
  // null = nadie ingresó. Vive en memoria: si se recarga la página, hay que volver a ingresar.
  const [socio, setSocio] = useState(null);

  function ingresar(socioEncontrado) {
    setSocio(socioEncontrado);
  }

  function salir() {
    setSocio(null);
  }

  return (
    <SocioContext.Provider value={{ socio, ingresar, salir }}>
      {children}
    </SocioContext.Provider>
  );
}

// Hook para leer el contexto: const { socio, ingresar, salir } = useSocio();
export function useSocio() {
  return useContext(SocioContext);
}
