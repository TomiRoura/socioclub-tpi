package com.socioclub.controller.web;

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

import com.socioclub.dto.InscripcionForm;
import com.socioclub.exception.RecursoNoEncontradoException;
import com.socioclub.exception.ReglaNegocioException;
import com.socioclub.model.Inscripcion;
import com.socioclub.service.ActividadService;
import com.socioclub.service.InscripcionService;
import com.socioclub.service.SocioService;

// Inscripción desde secretaría (RF6) y baja lógica (RF9).
// Las reglas RN1 a RN5 no están acá: las aplica InscripcionService, igual que para la API.
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

	// /inscripciones/nueva?actividadId=6 deja la actividad ya elegida
	@GetMapping("/nueva")
	public String nueva(@RequestParam(required = false) Long actividadId,
			@RequestParam(required = false) Long socioId, Model model) {
		InscripcionForm form = new InscripcionForm();
		form.setActividadId(actividadId);
		form.setSocioId(socioId);
		model.addAttribute("inscripcion", form);
		cargarCombos(model);
		return "inscripciones/formulario";
	}

	@PostMapping
	public String inscribir(@ModelAttribute("inscripcion") InscripcionForm form, BindingResult resultado, Model model,
			RedirectAttributes flash) {
		if (resultado.hasErrors()) {
			model.addAttribute("error", "Elegí un socio y una actividad");
			cargarCombos(model);
			return "inscripciones/formulario";
		}
		try {
			Inscripcion nueva = inscripcionService.inscribir(form.getSocioId(), form.getActividadId());
			flash.addFlashAttribute("exito", nueva.getSocio().getNombre() + " " + nueva.getSocio().getApellido()
					+ " quedó inscripto en " + nueva.getActividad().getNombre());
			return "redirect:/actividades/" + nueva.getActividad().getId() + "/inscriptos";
		} catch (ReglaNegocioException e) {
			// La vista muestra el mensaje de la regla que rechazó la inscripción
			model.addAttribute("error", e.getMessage());
			model.addAttribute("regla", e.getRegla());
		} catch (RecursoNoEncontradoException e) {
			model.addAttribute("error", "Elegí un socio y una actividad");
		}
		cargarCombos(model);
		return "inscripciones/formulario";
	}

	@PostMapping("/{id}/baja")
	public String darDeBaja(@PathVariable Long id, @RequestParam(required = false) String volverA,
			RedirectAttributes flash) {
		try {
			Inscripcion baja = inscripcionService.darDeBaja(id);
			flash.addFlashAttribute("exito", "Se dio de baja a " + baja.getSocio().getNombre() + " "
					+ baja.getSocio().getApellido() + " de " + baja.getActividad().getNombre());
			if ("socio".equals(volverA)) {
				return "redirect:/socios/" + baja.getSocio().getId() + "/editar";
			}
			return "redirect:/actividades/" + baja.getActividad().getId() + "/inscriptos";
		} catch (RecursoNoEncontradoException e) {
			flash.addFlashAttribute("error", e.getMessage());
			return "redirect:/actividades";
		}
	}

	private void cargarCombos(Model model) {
		model.addAttribute("socios", socioService.listarTodos());
		model.addAttribute("actividades", actividadService.listar(null));
	}
}
