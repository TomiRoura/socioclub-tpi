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

import com.socioclub.exception.DatosInvalidosException;
import com.socioclub.exception.RecursoNoEncontradoException;
import com.socioclub.exception.ReglaNegocioException;
import com.socioclub.model.Actividad;
import com.socioclub.service.ActividadService;
import com.socioclub.service.InscripcionService;

// Pantallas de secretaría para el ABM de actividades (RF4) y los inscriptos de cada una (RF7)
@Controller
@RequestMapping("/actividades")
public class ActividadController {

	private final ActividadService actividadService;
	private final InscripcionService inscripcionService;

	public ActividadController(ActividadService actividadService, InscripcionService inscripcionService) {
		this.actividadService = actividadService;
		this.inscripcionService = inscripcionService;
	}

	@GetMapping
	public String listar(@RequestParam(required = false) String disciplina, Model model) {
		model.addAttribute("actividades", actividadService.listar(disciplina));
		model.addAttribute("disciplinas", actividadService.listar(null).stream()
				.map(Actividad::getDisciplina).distinct().sorted().toList());
		model.addAttribute("disciplinaElegida", disciplina);
		return "actividades/lista";
	}

	@GetMapping("/nueva")
	public String nueva(Model model) {
		model.addAttribute("actividad", new Actividad());
		return "actividades/formulario";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
		try {
			model.addAttribute("actividad", actividadService.buscarPorId(id));
			return "actividades/formulario";
		} catch (RecursoNoEncontradoException e) {
			flash.addFlashAttribute("error", e.getMessage());
			return "redirect:/actividades";
		}
	}

	// Alta y modificación usan el mismo formulario: si viene id es una modificación
	@PostMapping("/guardar")
	public String guardar(@ModelAttribute("actividad") Actividad actividad, BindingResult resultado, Model model,
			RedirectAttributes flash) {
		// Si llega texto donde va un número (por ejemplo "abc" en el cupo), Spring no puede convertirlo:
		// en vez de una página de error 400 se vuelve al formulario con un aviso
		if (resultado.hasErrors()) {
			model.addAttribute("error", "Revisá los campos numéricos: edades, cupo y cuota tienen que ser números enteros");
			return "actividades/formulario";
		}
		boolean esNueva = actividad.getId() == null;
		try {
			Actividad guardada = actividadService.guardar(actividad);
			flash.addFlashAttribute("exito", (esNueva ? "Se creó la actividad " : "Se guardaron los cambios de ")
					+ guardada.getNombre());
			return "redirect:/actividades";
		} catch (DatosInvalidosException | RecursoNoEncontradoException e) {
			model.addAttribute("error", e.getMessage());
			return "actividades/formulario";
		}
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
		try {
			String nombre = actividadService.buscarPorId(id).getNombre();
			actividadService.eliminar(id);
			flash.addFlashAttribute("exito", "Se eliminó la actividad " + nombre);
		} catch (ReglaNegocioException | RecursoNoEncontradoException e) {
			flash.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/actividades";
	}

	@GetMapping("/{id}/inscriptos")
	public String inscriptos(@PathVariable Long id, Model model, RedirectAttributes flash) {
		try {
			model.addAttribute("actividad", actividadService.buscarPorId(id));
			model.addAttribute("inscripciones", inscripcionService.listarActivasDeLaActividad(id));
			return "actividades/inscriptos";
		} catch (RecursoNoEncontradoException e) {
			flash.addFlashAttribute("error", e.getMessage());
			return "redirect:/actividades";
		}
	}
}
