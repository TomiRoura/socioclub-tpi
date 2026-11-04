package com.socioclub.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socioclub.exception.RecursoNoEncontradoException;
import com.socioclub.exception.ReglaNegocioException;
import com.socioclub.model.Actividad;
import com.socioclub.model.EstadoCuota;
import com.socioclub.model.EstadoInscripcion;
import com.socioclub.model.Inscripcion;
import com.socioclub.model.Socio;
import com.socioclub.repository.InscripcionRepository;

// Acá están las reglas RN1 a RN5. Las usan igual la pantalla de secretaría y la API de la app,
// así que una inscripción se valida de la misma forma entre por donde entre.
@Service
public class InscripcionService {

	// RN5: si piden cambiar el máximo (por ejemplo a 4), se cambia solo acá
	// (y en app-socios/src/utils/constantes.js para el modo local de la app)
	public static final int MAX_ACTIVIDADES = 3;

	private final InscripcionRepository inscripcionRepository;
	private final SocioService socioService;
	private final ActividadService actividadService;

	public InscripcionService(InscripcionRepository inscripcionRepository, SocioService socioService,
			ActividadService actividadService) {
		this.inscripcionRepository = inscripcionRepository;
		this.socioService = socioService;
		this.actividadService = actividadService;
	}

	// Inscribe al socio en la actividad. Si alguna regla no se cumple lanza ReglaNegocioException.
	// El orden de los controles es el mismo que usa la app en modo local: RN4, RN2, RN5, RN3 y RN1.
	@Transactional
	public Inscripcion inscribir(Long socioId, Long actividadId) {
		Socio socio = socioService.buscarPorId(socioId);
		Actividad actividad = actividadService.buscarPorId(actividadId);

		// RN4: un socio moroso no puede inscribirse (sí puede consultar y darse de baja)
		if (socio.getEstadoCuota() == EstadoCuota.MOROSO) {
			throw new ReglaNegocioException("Debe regularizar su cuota en secretaría", "RN4");
		}

		// RN2: no puede tener dos inscripciones ACTIVAS en la misma actividad
		if (inscripcionRepository.existsBySocioIdAndActividadIdAndEstado(socio.getId(), actividad.getId(),
				EstadoInscripcion.ACTIVA)) {
			throw new ReglaNegocioException("El socio ya está inscripto en esta actividad", "RN2");
		}

		// RN5: como máximo 3 actividades activas
		long activasDelSocio = inscripcionRepository.countBySocioIdAndEstado(socio.getId(), EstadoInscripcion.ACTIVA);
		if (activasDelSocio >= MAX_ACTIVIDADES) {
			throw new ReglaNegocioException("Alcanzó el máximo de " + MAX_ACTIVIDADES + " actividades", "RN5");
		}

		// RN3: la edad de hoy tiene que estar dentro del rango de la actividad
		int edad = SocioService.calcularEdad(socio.getFechaNacimiento());
		if (edad < actividad.getEdadMinima() || edad > actividad.getEdadMaxima()) {
			throw new ReglaNegocioException("La edad permitida para esta actividad es de " + actividad.getEdadMinima()
					+ " a " + actividad.getEdadMaxima() + " años", "RN3");
		}

		// RN1: no puede haber más inscripciones ACTIVAS que el cupo máximo
		long inscriptos = actividadService.contarInscriptosActivos(actividad.getId());
		if (inscriptos >= actividad.getCupoMaximo()) {
			throw new ReglaNegocioException("La actividad no tiene cupo disponible", "RN1");
		}

		Inscripcion nueva = new Inscripcion(socio, actividad, LocalDate.now(), EstadoInscripcion.ACTIVA);
		Inscripcion guardada = inscripcionRepository.saveAndFlush(nueva);
		// La respuesta lleva la actividad con el cupo ya descontado
		actividadService.completarCupoDisponible(guardada.getActividad());
		return guardada;
	}

	// RF9: baja lógica, la inscripción no se borra, queda en estado BAJA y libera el cupo
	@Transactional
	public Inscripcion darDeBaja(Long inscripcionId) {
		Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
				.filter(i -> i.getEstado() == EstadoInscripcion.ACTIVA)
				.orElseThrow(() -> new RecursoNoEncontradoException("Inscripción activa no encontrada"));
		inscripcion.setEstado(EstadoInscripcion.BAJA);
		Inscripcion actualizada = inscripcionRepository.saveAndFlush(inscripcion);
		actividadService.completarCupoDisponible(actualizada.getActividad());
		return actualizada;
	}

	// Inscripciones ACTIVAS de un socio (app "Mi Club")
	@Transactional(readOnly = true)
	public List<Inscripcion> listarActivasDelSocio(Long socioId) {
		Socio socio = socioService.buscarPorId(socioId);
		List<Inscripcion> activas = inscripcionRepository.findBySocioIdAndEstado(socio.getId(), EstadoInscripcion.ACTIVA);
		activas.forEach(i -> actividadService.completarCupoDisponible(i.getActividad()));
		return activas;
	}

	// RF7: inscriptos activos de una actividad (pantalla de secretaría)
	@Transactional(readOnly = true)
	public List<Inscripcion> listarActivasDeLaActividad(Long actividadId) {
		Actividad actividad = actividadService.buscarPorId(actividadId);
		return inscripcionRepository.findByActividadIdAndEstado(actividad.getId(), EstadoInscripcion.ACTIVA);
	}
}
