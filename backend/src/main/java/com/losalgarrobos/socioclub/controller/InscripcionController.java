package com.losalgarrobos.socioclub.controller;

import com.losalgarrobos.socioclub.dto.InscripcionRequest;
import com.losalgarrobos.socioclub.model.Inscripcion;
import com.losalgarrobos.socioclub.service.ActividadService;
import com.losalgarrobos.socioclub.service.DatosInvalidosException;
import com.losalgarrobos.socioclub.service.InscripcionService;
import com.losalgarrobos.socioclub.service.ReglaNegocioException;
import com.losalgarrobos.socioclub.service.SocioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Inscripciones desde la secretaría. El controlador no valida ninguna regla: solo llama a
// InscripcionService.inscribir() (el mismo método que usa la API) y muestra el resultado.
@Controller
@RequestMapping("/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;
    private final SocioService socioService;
    private final ActividadService actividadService;

    public InscripcionController(InscripcionService inscripcionService, SocioService socioService,
                                 ActividadService actividadService) {
        this.inscripcionService = inscripcionService;
        this.socioService = socioService;
        this.actividadService = actividadService;
    }

    // Formulario: elegir socio y actividad. Se puede llegar con uno de los dos ya elegido.
    @GetMapping("/nueva")
    public String nueva(@RequestParam(required = false) Long socioId,
                        @RequestParam(required = false) Long actividadId,
                        Model model) {
        return mostrarFormulario(new InscripcionRequest(socioId, actividadId), model);
    }

    @PostMapping
    public String inscribir(@ModelAttribute("inscripcion") InscripcionRequest pedido,
                            Model model, RedirectAttributes redirect) {
        try {
            Inscripcion nueva = inscripcionService.inscribir(pedido.getSocioId(), pedido.getActividadId());
            redirect.addFlashAttribute("exito", "Inscripción registrada en " + nueva.getActividad().getNombre());
            return "redirect:/socios/" + pedido.getSocioId();
        } catch (ReglaNegocioException e) {
            // La regla rechazó la inscripción: se vuelve al formulario con el mensaje.
            model.addAttribute("error", e.getMessage());
            model.addAttribute("regla", e.getRegla());
            return mostrarFormulario(pedido, model);
        } catch (DatosInvalidosException e) {
            model.addAttribute("error", e.getMessage());
            return mostrarFormulario(pedido, model);
        }
    }

    // Baja lógica desde la secretaría. "volver" indica a qué pantalla regresar.
    @PostMapping("/{id}/baja")
    public String darDeBaja(@PathVariable Long id,
                            @RequestParam(defaultValue = "socio") String volver,
                            RedirectAttributes redirect) {
        Inscripcion inscripcion = inscripcionService.darDeBaja(id);
        redirect.addFlashAttribute("exito", "Baja registrada en " + inscripcion.getActividad().getNombre());
        if ("actividad".equals(volver)) {
            return "redirect:/actividades/" + inscripcion.getActividad().getId() + "/inscriptos";
        }
        return "redirect:/socios/" + inscripcion.getSocio().getId();
    }

    // Se muestran TODAS las actividades, también las llenas (marcadas "sin cupo"):
    // si se ocultaran, RN1 no se podría mostrar desde la secretaría.
    private String mostrarFormulario(InscripcionRequest pedido, Model model) {
        model.addAttribute("inscripcion", pedido);
        model.addAttribute("socios", socioService.listarTodos());
        model.addAttribute("actividades", actividadService.listar(null));
        return "inscripciones/formulario";
    }
}
