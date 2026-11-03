package com.losalgarrobos.socioclub.controller.api;

import com.losalgarrobos.socioclub.model.Actividad;
import com.losalgarrobos.socioclub.service.ActividadService;
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

import java.net.URI;
import java.util.List;

// @RestController = @Controller + @ResponseBody: lo que devuelve cada método se convierte en JSON
// (con Jackson) en lugar de buscar una vista. Usa el MISMO servicio que la secretaría.
@RestController
@RequestMapping("/api/actividades")
public class ActividadRestController {

    private final ActividadService actividadService;

    public ActividadRestController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    // GET /api/actividades y GET /api/actividades?disciplina=Fútbol -> 200
    @GetMapping
    public List<Actividad> listar(@RequestParam(required = false) String disciplina) {
        return actividadService.listar(disciplina);
    }

    // GET /api/actividades/{id} -> 200 / 404
    @GetMapping("/{id}")
    public Actividad obtener(@PathVariable Long id) {
        return actividadService.buscarPorId(id);
    }

    // POST /api/actividades -> 201 Created, con la cabecera Location apuntando al recurso nuevo
    @PostMapping
    public ResponseEntity<Actividad> crear(@RequestBody Actividad actividad) {
        Actividad creada = actividadService.crear(actividad);
        return ResponseEntity.created(URI.create("/api/actividades/" + creada.getId())).body(creada);
    }

    // PUT /api/actividades/{id} -> 200 / 404
    @PutMapping("/{id}")
    public Actividad actualizar(@PathVariable Long id, @RequestBody Actividad actividad) {
        return actividadService.actualizar(id, actividad);
    }

    // DELETE /api/actividades/{id} -> 204 No Content / 404 / 409 si tiene inscriptos activos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        actividadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
