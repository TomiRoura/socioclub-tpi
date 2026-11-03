package com.losalgarrobos.socioclub.controller;

import com.losalgarrobos.socioclub.model.Actividad;
import com.losalgarrobos.socioclub.service.ActividadService;
import com.losalgarrobos.socioclub.service.DatosInvalidosException;
import com.losalgarrobos.socioclub.service.InscripcionService;
import com.losalgarrobos.socioclub.service.ReglaNegocioException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Controlador MVC (devuelve nombres de vistas Thymeleaf, no JSON).
// No accede a los repositorios: todo pasa por la capa de servicio.
@Controller
@RequestMapping("/actividades")
public class ActividadController {

    private final ActividadService actividadService;
    private final InscripcionService inscripcionService;

    public ActividadController(ActividadService actividadService, InscripcionService inscripcionService) {
        this.actividadService = actividadService;
        this.inscripcionService = inscripcionService;
    }

    // Listado con cupo disponible y filtro opcional por disciplina (RF4).
    @GetMapping
    public String listar(@RequestParam(required = false) String disciplina, Model model) {
        model.addAttribute("actividades", actividadService.listar(disciplina));
        model.addAttribute("disciplinas", ActividadService.DISCIPLINAS);
        model.addAttribute("disciplinaElegida", disciplina);
        return "actividades/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        return mostrarFormulario(new Actividad(), model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return mostrarFormulario(actividadService.buscarPorId(id), model);
    }

    // Alta y modificación usan el mismo formulario: si viene id es una edición.
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("actividad") Actividad actividad, BindingResult binding,
                          Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            model.addAttribute("error", "Revisá los campos numéricos: edades, cupo y cuota tienen que ser números enteros");
            return mostrarFormulario(actividad, model);
        }
        try {
            if (actividad.getId() == null) {
                actividadService.crear(actividad);
                redirect.addFlashAttribute("exito", "Actividad creada");
            } else {
                actividadService.actualizar(actividad.getId(), actividad);
                redirect.addFlashAttribute("exito", "Actividad modificada");
            }
            return "redirect:/actividades";
        } catch (DatosInvalidosException e) {
            model.addAttribute("error", e.getMessage());
            return mostrarFormulario(actividad, model);
        }
    }

    // Baja de una actividad: solo si no tiene inscriptos activos.
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            actividadService.eliminar(id);
            redirect.addFlashAttribute("exito", "Actividad eliminada");
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/actividades";
    }

    // Inscriptos activos de la actividad y su cupo disponible (RF7).
    @GetMapping("/{id}/inscriptos")
    public String inscriptos(@PathVariable Long id, Model model) {
        model.addAttribute("actividad", actividadService.buscarPorId(id));
        model.addAttribute("inscripciones", inscripcionService.listarInscriptosDeActividad(id));
        return "actividades/inscriptos";
    }

    private String mostrarFormulario(Actividad actividad, Model model) {
        model.addAttribute("actividad", actividad);
        model.addAttribute("disciplinas", ActividadService.DISCIPLINAS);
        return "actividades/formulario";
    }
}
