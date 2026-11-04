package com.socioclub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.socioclub.exception.RecursoNoEncontradoException;
import com.socioclub.exception.ReglaNegocioException;
import com.socioclub.model.EstadoInscripcion;
import com.socioclub.model.Inscripcion;

// Prueba las reglas RN1 a RN5 contra la base cargada con sql/socioclub.sql.
// @Transactional deshace todo al terminar cada prueba, así la base queda como estaba.
// Ids de socios y actividades: ver la tabla de DNI del README.
@SpringBootTest
@Transactional
class InscripcionServiceTest {

	private static final long LUCIA = 1, MATEO = 2, JORGE = 4, SERGIO = 5, CAMILA = 10, GABRIEL = 26;
	private static final long FUTBOL = 1, VOLEY = 2, NATACION = 4, AJEDREZ = 6, YOGA = 8;

	@Autowired
	private InscripcionService inscripcionService;

	@Autowired
	private ActividadService actividadService;

	private String reglaQueFalla(long socioId, long actividadId) {
		return assertThrows(ReglaNegocioException.class, () -> inscripcionService.inscribir(socioId, actividadId))
				.getRegla();
	}

	@Test
	void inscribeYDescuentaElCupo() {
		Inscripcion nueva = inscripcionService.inscribir(LUCIA, AJEDREZ);
		assertEquals(EstadoInscripcion.ACTIVA, nueva.getEstado());
		assertEquals(0, nueva.getActividad().getCupoDisponible());
	}

	@Test
	void rn1SinCupo() {
		assertEquals("RN1", reglaQueFalla(CAMILA, YOGA));
	}

	@Test
	void rn2Duplicado() {
		assertEquals("RN2", reglaQueFalla(MATEO, FUTBOL));
	}

	@Test
	void rn3EdadFueraDeRango() {
		assertEquals("RN3", reglaQueFalla(MATEO, VOLEY));
	}

	@Test
	void rn4Moroso() {
		assertEquals("RN4", reglaQueFalla(JORGE, NATACION));
	}

	@Test
	void rn5MaximoDeActividades() {
		assertEquals("RN5", reglaQueFalla(SERGIO, NATACION));
	}

	@Test
	void laBajaLiberaElCupo() {
		Inscripcion yogaDeGabriel = inscripcionService.listarActivasDelSocio(GABRIEL).stream()
				.filter(i -> i.getActividad().getId() == YOGA).findFirst().orElseThrow();
		Inscripcion baja = inscripcionService.darDeBaja(yogaDeGabriel.getId());
		assertEquals(EstadoInscripcion.BAJA, baja.getEstado());
		assertEquals(1, baja.getActividad().getCupoDisponible());
		// Con el lugar libre, Camila ya puede entrar
		inscripcionService.inscribir(CAMILA, YOGA);
	}

	@Test
	void noSePuedeDarDeBajaDosVeces() {
		Inscripcion nueva = inscripcionService.inscribir(LUCIA, AJEDREZ);
		inscripcionService.darDeBaja(nueva.getId());
		assertThrows(RecursoNoEncontradoException.class, () -> inscripcionService.darDeBaja(nueva.getId()));
	}

	@Test
	void socioInexistente() {
		assertThrows(RecursoNoEncontradoException.class, () -> inscripcionService.inscribir(999L, AJEDREZ));
	}

	@Test
	void noSeEliminaUnaActividadConInscriptos() {
		assertThrows(ReglaNegocioException.class, () -> actividadService.eliminar(FUTBOL));
	}
}
