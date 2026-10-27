import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import TarjetaActividad from './TarjetaActividad';

const futbol = {
  id: 1,
  nombre: 'Fútbol infantil',
  disciplina: 'Fútbol',
  dias: 'Lun y Mié',
  horario: '18:00',
  cupoDisponible: 12,
  cuotaMensual: 18000,
};

// La tarjeta usa <Link>, que solo funciona dentro de un router. En los tests se usa
// MemoryRouter: un router que guarda la URL en memoria, sin navegador.
// "future" es el mismo que en App.js (evita los avisos de React Router 7 en la consola).
function dibujarTarjeta(actividad, onInscribir) {
  render(
    <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <TarjetaActividad actividad={actividad} onInscribir={onInscribir} />
    </MemoryRouter>
  );
}

test('muestra el nombre de la actividad', () => {
  dibujarTarjeta(futbol, jest.fn());
  expect(screen.getByText('Fútbol infantil')).toBeInTheDocument();
});

test('con cupo 0 muestra "Sin cupo" y el botón está deshabilitado', () => {
  const onInscribir = jest.fn(); // función "espía": registra si la llamaron
  dibujarTarjeta({ ...futbol, cupoDisponible: 0 }, onInscribir);

  expect(screen.getByText('Sin cupo')).toBeInTheDocument();
  const boton = screen.getByRole('button', { name: 'Inscribirme' });
  expect(boton).toBeDisabled();

  // Un botón deshabilitado no dispara onClick.
  fireEvent.click(boton);
  expect(onInscribir).not.toHaveBeenCalled();
});

test('con cupo, al hacer clic en "Inscribirme" le pasa la actividad al padre', () => {
  const onInscribir = jest.fn();
  dibujarTarjeta(futbol, onInscribir);

  expect(screen.queryByText('Sin cupo')).not.toBeInTheDocument();
  fireEvent.click(screen.getByRole('button', { name: 'Inscribirme' }));
  expect(onInscribir).toHaveBeenCalledWith(futbol);
});
