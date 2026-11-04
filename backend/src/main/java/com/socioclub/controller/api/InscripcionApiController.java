package com.socioclub.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.socioclub.dto.InscripcionForm;
import com.socioclub.model.Inscripcion;
import com.socioclub.service.InscripcionService;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionApiController {

	private final InscripcionService inscripcionService;

	public InscripcionApiController(InscripcionService inscripcionService) {
		this.inscripcionService = inscripcionService;
	}

	// POST /api/inscripciones con { "socioId": 3, "actividadId": 1 } -> 201 / 404 / 409 (RN1 a RN5)
	@PostMapping
	public ResponseEntity<Inscripcion> inscribir(@RequestBody InscripcionForm pedido) {
		Inscripcion nueva = inscripcionService.inscribir(pedido.getSocioId(), pedido.getActividadId());
		return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
	}

	// PUT /api/inscripciones/{id}/baja -> 200 / 404 (baja lógica)
	@PutMapping("/{id}/baja")
	public Inscripcion darDeBaja(@PathVariable Long id) {
		return inscripcionService.darDeBaja(id);
	}
}
