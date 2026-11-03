package com.losalgarrobos.socioclub.controller.api;

import com.losalgarrobos.socioclub.model.Inscripcion;
import com.losalgarrobos.socioclub.model.Socio;
import com.losalgarrobos.socioclub.service.InscripcionService;
import com.losalgarrobos.socioclub.service.SocioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/socios")
public class SocioRestController {

    private final SocioService socioService;
    private final InscripcionService inscripcionService;

    public SocioRestController(SocioService socioService, InscripcionService inscripcionService) {
        this.socioService = socioService;
        this.inscripcionService = inscripcionService;
    }

    // GET /api/socios/dni/{dni} -> 200 / 404. Es el "ingreso" de la app: no hay contraseña.
    @GetMapping("/dni/{dni}")
    public Socio buscarPorDni(@PathVariable String dni) {
        return socioService.buscarPorDni(dni);
    }

    // GET /api/socios/{id}/inscripciones -> 200 / 404. Solo las ACTIVA, con la actividad adentro.
    @GetMapping("/{id}/inscripciones")
    public List<Inscripcion> inscripcionesActivas(@PathVariable Long id) {
        return inscripcionService.listarActivasDeSocio(id);
    }
}
