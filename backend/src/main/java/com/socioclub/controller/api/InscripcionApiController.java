package com.socioclub.controller.api;

import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.socioclub.exception.ReglaNegocioException;
import com.socioclub.model.Actividad;
import com.socioclub.model.EstadoInscripcion;
import com.socioclub.model.Inscripcion;
import com.socioclub.model.Socio;
import com.socioclub.repository.ActividadRepository;
import com.socioclub.repository.InscripcionRepository;
import com.socioclub.repository.SocioRepository;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionApiController {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private ActividadRepository actividadRepository;

    @PostMapping
    public ResponseEntity<Inscripcion> inscribir(@RequestBody Map<String, Long> body) {
        Long socioId = body.get("socioId");
        Long actividadId = body.get("actividadId");

        Socio socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new ReglaNegocioException("Socio no encontrado", "RN_SOCIO"));
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ReglaNegocioException("Actividad no encontrada", "RN_ACTIVIDAD"));

        // Validación de máximo 3 actividades (RN5 de ejemplo integrada)
        long activasCount = inscripcionRepository.countBySocioIdAndEstado(socioId, EstadoInscripcion.ACTIVA);
        if (activasCount >= 3) {
            throw new ReglaNegocioException("Alcanzó el máximo de 3 actividades", "RN5");
        }

        // Validación de duplicados (RN2)
        boolean yaInscripto = inscripcionRepository.existsBySocioIdAndActividadIdAndEstado(socioId, actividadId, EstadoInscripcion.ACTIVA);
        if (yaInscripto) {
            throw new ReglaNegocioException("El socio ya está inscripto en esta actividad", "RN2");
        }

        Inscripcion nueva = new Inscripcion(socio, actividad, LocalDate.now(), EstadoInscripcion.ACTIVA);
        Inscripcion guardada = inscripcionRepository.save(nueva);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<Inscripcion> darDeBaja(@PathVariable Long id) {
        return inscripcionRepository.findById(id).map(inscripcion -> {
            inscripcion.setEstado(EstadoInscripcion.BAJA);
            Inscripcion actualizada = inscripcionRepository.save(inscripcion);
            return ResponseEntity.ok(actualizada);
        }).orElse(ResponseEntity.notFound().build());
    }
}