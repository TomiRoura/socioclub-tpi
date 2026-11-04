package com.socioclub.controller.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.socioclub.model.Inscripcion;
import com.socioclub.model.Socio;
import com.socioclub.service.InscripcionService;
import com.socioclub.service.SocioService;

@RestController
@RequestMapping("/api/socios")
public class SocioApiController {

	private final SocioService socioService;
	private final InscripcionService inscripcionService;

	public SocioApiController(SocioService socioService, InscripcionService inscripcionService) {
		this.socioService = socioService;
		this.inscripcionService = inscripcionService;
	}

	// GET /api/socios/dni/{dni} -> 200 / 404 (ingreso a la app)
	@GetMapping("/dni/{dni}")
	public Socio buscarPorDni(@PathVariable String dni) {
		return socioService.buscarPorDni(dni);
	}

	// GET /api/socios/{id}/inscripciones -> 200 / 404 (solo las ACTIVAS)
	@GetMapping("/{id}/inscripciones")
	public List<Inscripcion> inscripcionesActivas(@PathVariable Long id) {
		return inscripcionService.listarActivasDelSocio(id);
	}
}
