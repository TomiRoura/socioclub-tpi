package com.socioclub.controller.api;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.socioclub.model.EstadoInscripcion;
import com.socioclub.model.Inscripcion;
import com.socioclub.model.Socio;
import com.socioclub.repository.InscripcionRepository;
import com.socioclub.repository.SocioRepository;

@RestController
@RequestMapping("/api/socios")
public class SocioApiController {

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Socio> buscarPorDni(@PathVariable String dni) {
        return socioRepository.findByDni(dni)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/inscripciones")
    public ResponseEntity<List<Inscripcion>> obtenerInscripcionesActivas(@PathVariable Long id) {
        if (!socioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        List<Inscripcion> activas = inscripcionRepository.findBySocioIdAndEstado(id, EstadoInscripcion.ACTIVA);
        return ResponseEntity.ok(activas);
    }
}