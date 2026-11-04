package com.socioclub.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socioclub.exception.DatosInvalidosException;
import com.socioclub.exception.RecursoNoEncontradoException;
import com.socioclub.exception.ReglaNegocioException;
import com.socioclub.model.Actividad;
import com.socioclub.model.EstadoInscripcion;
import com.socioclub.repository.ActividadRepository;
import com.socioclub.repository.InscripcionRepository;

// Lógica de las actividades. La usan tanto las pantallas de secretaría como la API REST.
@Service
public class ActividadService {

	private final ActividadRepository actividadRepository;
	private final InscripcionRepository inscripcionRepository;

	// Inyección por constructor: Spring crea el bean y le pasa los repositorios
	public ActividadService(ActividadRepository actividadRepository, InscripcionRepository inscripcionRepository) {
		this.actividadRepository = actividadRepository;
		this.inscripcionRepository = inscripcionRepository;
	}

	// Todas las actividades (o las de una disciplina) con su cupo disponible calculado
	@Transactional(readOnly = true)
	public List<Actividad> listar(String disciplina) {
		List<Actividad> actividades;
		if (disciplina != null && !disciplina.isBlank()) {
			actividades = actividadRepository.findByDisciplina(disciplina);
		} else {
			actividades = actividadRepository.findAll(Sort.by("id"));
		}
		actividades.forEach(this::completarCupoDisponible);
		return actividades;
	}

	@Transactional(readOnly = true)
	public Actividad buscarPorId(Long id) {
		if (id == null) {
			throw new RecursoNoEncontradoException("Actividad no encontrada");
		}
		Actividad actividad = actividadRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Actividad no encontrada"));
		completarCupoDisponible(actividad);
		return actividad;
	}

	// Inscripciones ACTIVAS de la actividad (usa la consulta @Query del repositorio)
	public long contarInscriptosActivos(Long actividadId) {
		return inscripcionRepository.contarPorActividadYEstado(actividadId, EstadoInscripcion.ACTIVA);
	}

	// El cupo disponible no está en la tabla: es el cupo máximo menos los inscriptos activos
	public Actividad completarCupoDisponible(Actividad actividad) {
		long activos = contarInscriptosActivos(actividad.getId());
		actividad.setCupoDisponible((int) Math.max(0, actividad.getCupoMaximo() - activos));
		return actividad;
	}

	// Alta (sin id) o modificación (con id)
	@Transactional
	public Actividad guardar(Actividad actividad) {
		validar(actividad);
		if (actividad.getId() != null) {
			Actividad existente = actividadRepository.findById(actividad.getId())
					.orElseThrow(() -> new RecursoNoEncontradoException("Actividad no encontrada"));
			long activos = contarInscriptosActivos(existente.getId());
			if (actividad.getCupoMaximo() < activos) {
				throw new DatosInvalidosException("El cupo no puede ser menor que los " + activos
						+ " inscriptos activos que ya tiene la actividad");
			}
			existente.setNombre(actividad.getNombre().trim());
			existente.setDisciplina(actividad.getDisciplina().trim());
			existente.setProfesor(actividad.getProfesor().trim());
			existente.setDias(actividad.getDias().trim());
			existente.setHorario(actividad.getHorario().trim());
			existente.setEdadMinima(actividad.getEdadMinima());
			existente.setEdadMaxima(actividad.getEdadMaxima());
			existente.setCupoMaximo(actividad.getCupoMaximo());
			existente.setCuotaMensual(actividad.getCuotaMensual());
			return completarCupoDisponible(actividadRepository.save(existente));
		}
		return completarCupoDisponible(actividadRepository.save(actividad));
	}

	// Solo se puede eliminar si no tiene inscriptos activos (las inscripciones en BAJA se borran en cascada)
	@Transactional
	public void eliminar(Long id) {
		Actividad actividad = buscarPorId(id);
		if (contarInscriptosActivos(id) > 0) {
			throw new ReglaNegocioException("No se puede eliminar una actividad que tiene inscriptos activos",
					"INSCRIPTOS_ACTIVOS");
		}
		actividadRepository.delete(actividad);
	}

	private void validar(Actividad a) {
		if (estaVacio(a.getNombre()) || estaVacio(a.getDisciplina()) || estaVacio(a.getProfesor())
				|| estaVacio(a.getDias()) || estaVacio(a.getHorario())) {
			throw new DatosInvalidosException("Nombre, disciplina, profesor, días y horario son obligatorios");
		}
		if (a.getEdadMinima() == null || a.getEdadMaxima() == null || a.getCupoMaximo() == null
				|| a.getCuotaMensual() == null) {
			throw new DatosInvalidosException("Edades, cupo y cuota son obligatorios");
		}
		if (a.getEdadMinima() < 0 || a.getEdadMaxima() < a.getEdadMinima()) {
			throw new DatosInvalidosException("La edad máxima tiene que ser mayor o igual que la mínima");
		}
		if (a.getCupoMaximo() <= 0) {
			throw new DatosInvalidosException("El cupo tiene que ser mayor que cero");
		}
		if (a.getCuotaMensual() < 0) {
			throw new DatosInvalidosException("La cuota no puede ser negativa");
		}
		if (!a.getHorario().trim().matches("^([01]?\\d|2[0-3]):[0-5]\\d$")) {
			throw new DatosInvalidosException("El horario tiene que tener el formato HH:MM (por ejemplo 18:00)");
		}
	}

	private boolean estaVacio(String texto) {
		return texto == null || texto.isBlank();
	}
}
