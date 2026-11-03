package com.socioclub.controller.api;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.socioclub.model.Actividad;
import com.socioclub.repository.ActividadRepository;

@RestController
@RequestMapping("/api/actividades")
public class ActividadApiController {

    @Autowired
    private ActividadRepository actividadRepository;

    @GetMapping
    public List<Actividad> listarActividades(@RequestParam(required = false) String disciplina) {
        if (disciplina != null && !disciplina.isEmpty()) {
            return actividadRepository.findByDisciplina(disciplina);
        }
        return actividadRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Actividad> obtenerActividadPorId(@PathVariable Long id) {
        return actividadRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Actividad> crearActividad(@RequestBody Actividad actividad) {
        Actividad nueva = actividadRepository.save(actividad);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Actividad> modificarActividad(@PathVariable Long id, @RequestBody Actividad detalles) {
        return actividadRepository.findById(id).map(actividad -> {
            actividad.setNombre(detalles.getNombre());
            actividad.setDisciplina(detalles.getDisciplina());
            actividad.setProfesor(detalles.getProfesor());
            actividad.setDias(detalles.getDias());
            actividad.setHorario(detalles.getHorario());
            actividad.setEdadMinima(detalles.getEdadMinima());
            actividad.setEdadMaxima(detalles.getEdadMaxima());
            actividad.setCupoMaximo(detalles.getCupoMaximo());
            actividad.setCuotaMensual(detalles.getCuotaMensual());
            return ResponseEntity.ok(actividadRepository.save(actividad));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarActividad(@PathVariable Long id) {
        if (actividadRepository.existsById(id)) {
            actividadRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}