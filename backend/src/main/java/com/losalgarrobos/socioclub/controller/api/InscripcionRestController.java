package com.losalgarrobos.socioclub.controller.api;

import com.losalgarrobos.socioclub.dto.InscripcionRequest;
import com.losalgarrobos.socioclub.model.Inscripcion;
import com.losalgarrobos.socioclub.service.InscripcionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionRestController {

    private final InscripcionService inscripcionService;

    public InscripcionRestController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    // POST /api/inscripciones con { "socioId": 3, "actividadId": 1 }
    // -> 201 con la inscripción (actividad adentro, cupoDisponible ya descontado)
    // -> 404 si no existe el socio o la actividad
    // -> 409 { "error": "...", "regla": "RN1" } si una regla la rechaza
    @PostMapping
    public ResponseEntity<Inscripcion> inscribir(@RequestBody InscripcionRequest pedido) {
        Inscripcion nueva = inscripcionService.inscribir(pedido.getSocioId(), pedido.getActividadId());
        return ResponseEntity.created(URI.create("/api/inscripciones/" + nueva.getId())).body(nueva);
    }

    // PUT /api/inscripciones/{id}/baja -> 200 / 404. Baja lógica: el estado pasa a BAJA.
    @PutMapping("/{id}/baja")
    public Inscripcion darDeBaja(@PathVariable Long id) {
        return inscripcionService.darDeBaja(id);
    }
}
