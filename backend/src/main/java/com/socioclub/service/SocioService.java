package com.socioclub.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socioclub.exception.DatosInvalidosException;
import com.socioclub.exception.RecursoNoEncontradoException;
import com.socioclub.model.EstadoCuota;
import com.socioclub.model.Socio;
import com.socioclub.repository.SocioRepository;

@Service
public class SocioService {

	// Cantidad de socios por página en el listado de secretaría
	public static final int TAMANIO_PAGINA = 10;

	private final SocioRepository socioRepository;

	public SocioService(SocioRepository socioRepository) {
		this.socioRepository = socioRepository;
	}

	// Listado paginado con búsqueda por apellido; si no se escribe nada, trae todos
	@Transactional(readOnly = true)
	public Page<Socio> buscarPorApellido(String apellido, int pagina) {
		String filtro = apellido == null ? "" : apellido.trim();
		Pageable pageable = PageRequest.of(Math.max(pagina, 0), TAMANIO_PAGINA, Sort.by("apellido", "nombre"));
		return socioRepository.findByApellidoContainingIgnoreCase(filtro, pageable);
	}

	// Para el combo del formulario de inscripción
	@Transactional(readOnly = true)
	public List<Socio> listarTodos() {
		return socioRepository.findAll(Sort.by("apellido", "nombre"));
	}

	@Transactional(readOnly = true)
	public Socio buscarPorId(Long id) {
		if (id == null) {
			throw new RecursoNoEncontradoException("Socio no encontrado");
		}
		return socioRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Socio no encontrado"));
	}

	// Ingreso a la app de socios
	@Transactional(readOnly = true)
	public Socio buscarPorDni(String dni) {
		return socioRepository.findByDni(dni).orElseThrow(() -> new RecursoNoEncontradoException("Socio no encontrado"));
	}

	// Edad en años cumplidos a la fecha de hoy (la usa RN3)
	public static int calcularEdad(LocalDate fechaNacimiento) {
		return Period.between(fechaNacimiento, LocalDate.now()).getYears();
	}

	// Alta (sin id) o modificación (con id)
	@Transactional
	public Socio guardar(Socio socio) {
		validar(socio);
		socio.setDni(socio.getDni().trim());

		// El DNI es único: no puede estar cargado en otro socio
		Optional<Socio> conMismoDni = socioRepository.findByDni(socio.getDni());
		if (conMismoDni.isPresent() && !conMismoDni.get().getId().equals(socio.getId())) {
			throw new DatosInvalidosException("Ya existe un socio con el DNI " + socio.getDni());
		}

		if (socio.getId() != null) {
			Socio existente = buscarPorId(socio.getId());
			existente.setDni(socio.getDni());
			existente.setNombre(socio.getNombre().trim());
			existente.setApellido(socio.getApellido().trim());
			existente.setFechaNacimiento(socio.getFechaNacimiento());
			existente.setEmail(vacioANull(socio.getEmail()));
			existente.setTelefono(vacioANull(socio.getTelefono()));
			existente.setEstadoCuota(socio.getEstadoCuota());
			return socioRepository.save(existente);
		}
		socio.setEmail(vacioANull(socio.getEmail()));
		socio.setTelefono(vacioANull(socio.getTelefono()));
		return socioRepository.save(socio);
	}

	private void validar(Socio s) {
		// Misma regla que RN7 del lado del cliente: 7 u 8 dígitos, sin puntos
		if (s.getDni() == null || !s.getDni().trim().matches("^\\d{7,8}$")) {
			throw new DatosInvalidosException("El DNI tiene que tener 7 u 8 números, sin puntos");
		}
		if (s.getNombre() == null || s.getNombre().isBlank() || s.getApellido() == null || s.getApellido().isBlank()) {
			throw new DatosInvalidosException("Nombre y apellido son obligatorios");
		}
		if (s.getFechaNacimiento() == null) {
			throw new DatosInvalidosException("La fecha de nacimiento es obligatoria");
		}
		if (s.getFechaNacimiento().isAfter(LocalDate.now())) {
			throw new DatosInvalidosException("La fecha de nacimiento no puede ser futura");
		}
		if (s.getEmail() != null && !s.getEmail().isBlank() && !s.getEmail().trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
			throw new DatosInvalidosException("El email no es válido");
		}
		if (s.getEstadoCuota() == null) {
			s.setEstadoCuota(EstadoCuota.AL_DIA);
		}
	}

	private String vacioANull(String texto) {
		return texto == null || texto.isBlank() ? null : texto.trim();
	}
}
