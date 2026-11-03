package com.losalgarrobos.socioclub.controller;

import com.losalgarrobos.socioclub.model.Actividad;
import com.losalgarrobos.socioclub.service.ActividadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

// Pantalla de inicio de la secretaría: accesos rápidos y ocupación de cada actividad.
@Controller
public class InicioController {

    private final ActividadService actividadService;

    public InicioController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        List<Actividad> actividades = actividadService.listar(null);
        int cupoTotal = actividades.stream().mapToInt(Actividad::getCupoMaximo).sum();
        int libresTotal = actividades.stream().mapToInt(Actividad::getCupoDisponible).sum();
        model.addAttribute("actividades", actividades);
        model.addAttribute("inscriptosActivos", cupoTotal - libresTotal);
        model.addAttribute("lugaresLibres", libresTotal);
        model.addAttribute("sinCupo", actividades.stream().filter(a -> a.getCupoDisponible() == 0).count());
        return "index";
    }
}
