package com.losalgarrobos.socioclub.controller;

import com.losalgarrobos.socioclub.model.EstadoCuota;
import com.losalgarrobos.socioclub.model.Inscripcion;
import com.losalgarrobos.socioclub.model.Socio;
import com.losalgarrobos.socioclub.service.DatosInvalidosException;
import com.losalgarrobos.socioclub.service.InscripcionService;
import com.losalgarrobos.socioclub.service.SocioService;
import org.springframework.data.domain.Page;
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

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/socios")
public class SocioController {

    private final SocioService socioService;
    private final InscripcionService inscripcionService;

    public SocioController(SocioService socioService, InscripcionService inscripcionService) {
        this.socioService = socioService;
        this.inscripcionService = inscripcionService;
    }

    // Listado paginado con búsqueda por apellido (RF5). Ej.: /socios?apellido=gomez&pagina=0
    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String apellido,
                         @RequestParam(defaultValue = "0") int pagina,
                         Model model) {
        Page<Socio> socios = socioService.listarPaginado(apellido, pagina);
        model.addAttribute("socios", socios);
        model.addAttribute("apellido", apellido);
        model.addAttribute("hoy", LocalDate.now());
        return "socios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        return mostrarFormulario(new Socio(), model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return mostrarFormulario(socioService.buscarPorId(id), model);
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("socio") Socio socio, BindingResult binding,
                          Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            model.addAttribute("error", "Revisá la fecha de nacimiento");
            return mostrarFormulario(socio, model);
        }
        try {
            Socio guardado;
            if (socio.getId() == null) {
                guardado = socioService.crear(socio);
                redirect.addFlashAttribute("exito", "Socio dado de alta");
            } else {
                guardado = socioService.actualizar(socio.getId(), socio);
                redirect.addFlashAttribute("exito", "Datos del socio modificados");
            }
            return "redirect:/socios/" + guardado.getId();
        } catch (DatosInvalidosException e) {
            model.addAttribute("error", e.getMessage());
            return mostrarFormulario(socio, model);
        }
    }

    // Ficha del socio: sus actividades activas, cuota estimada (RN6) y botón de baja (RF9).
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Socio socio = socioService.buscarPorId(id);
        List<Inscripcion> activas = inscripcionService.listarActivasDeSocio(id);
        model.addAttribute("socio", socio);
        model.addAttribute("edad", socio.calcularEdad(LocalDate.now()));
        model.addAttribute("inscripciones", activas);
        model.addAttribute("cuota", inscripcionService.calcularCuotaMensual(activas));
        model.addAttribute("descuento", inscripcionService.porcentajeDescuento(activas.size()));
        model.addAttribute("maxActividades", InscripcionService.MAX_ACTIVIDADES);
        return "socios/detalle";
    }

    private String mostrarFormulario(Socio socio, Model model) {
        model.addAttribute("socio", socio);
        model.addAttribute("estadosCuota", EstadoCuota.values());
        return "socios/formulario";
    }
}
