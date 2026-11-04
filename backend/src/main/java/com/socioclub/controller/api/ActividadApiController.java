package com.socioclub.controller.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.socioclub.model.Actividad;
import com.socioclub.service.ActividadService;

// Los controladores REST no tocan los repositorios: todo pasa por la capa de servicio,
// la misma que usan las pantallas de secretaría. Los errores los convierte RestExceptionHandler.
@RestController
@RequestMapping("/api/actividades")
public class ActividadApiController {

	private final ActividadService actividadService;

	public ActividadApiController(ActividadService actividadService) {
		this.actividadService = actividadService;
	}

	// GET /api/actividades?disciplina=Fútbol -> 200 (con el cupo disponible de cada una)
	@GetMapping
	public List<Actividad> listar(@RequestParam(required = false) String disciplina) {
		return actividadService.listar(disciplina);
	}

	// GET /api/actividades/{id} -> 200 / 404
	@GetMapping("/{id}")
	public Actividad obtener(@PathVariable Long id) {
		return actividadService.buscarPorId(id);
	}

	// POST /api/actividades -> 201 (400 si faltan datos)
	@PostMapping
	public ResponseEntity<Actividad> crear(@RequestBody Actividad actividad) {
		actividad.setId(null); // el id lo genera la base
		return ResponseEntity.status(HttpStatus.CREATED).body(actividadService.guardar(actividad));
	}

	// PUT /api/actividades/{id} -> 200 / 404
	@PutMapping("/{id}")
	public Actividad modificar(@PathVariable Long id, @RequestBody Actividad actividad) {
		actividad.setId(id);
		return actividadService.guardar(actividad);
	}

	// DELETE /api/actividades/{id} -> 204 / 404 / 409 (si tiene inscriptos activos)
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		actividadService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
