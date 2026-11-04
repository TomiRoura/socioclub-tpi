package com.socioclub.controller.web;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.socioclub.exception.DatosInvalidosException;
import com.socioclub.exception.RecursoNoEncontradoException;
import com.socioclub.model.EstadoCuota;
import com.socioclub.model.Socio;
import com.socioclub.service.InscripcionService;
import com.socioclub.service.SocioService;

// Pantallas de secretaría para socios (RF5): listado paginado con búsqueda, alta y edición
@Controller
@RequestMapping("/socios")
public class SocioController {

	private final SocioService socioService;
	private final InscripcionService inscripcionService;

	public SocioController(SocioService socioService, InscripcionService inscripcionService) {
		this.socioService = socioService;
		this.inscripcionService = inscripcionService;
	}

	// /socios?apellido=gomez&pagina=0
	@GetMapping
	public String listar(@RequestParam(defaultValue = "") String apellido,
			@RequestParam(defaultValue = "0") int pagina, Model model) {
		Page<Socio> pagSocios = socioService.buscarPorApellido(apellido, pagina);
		model.addAttribute("pagina", pagSocios);
		model.addAttribute("apellido", apellido);
		return "socios/lista";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("socio", new Socio());
		model.addAttribute("estados", EstadoCuota.values());
		return "socios/formulario";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
		try {
			model.addAttribute("socio", socioService.buscarPorId(id));
			model.addAttribute("estados", EstadoCuota.values());
			model.addAttribute("inscripciones", inscripcionService.listarActivasDelSocio(id));
			return "socios/formulario";
		} catch (RecursoNoEncontradoException e) {
			flash.addFlashAttribute("error", e.getMessage());
			return "redirect:/socios";
		}
	}

	@PostMapping("/guardar")
	public String guardar(@ModelAttribute("socio") Socio socio, Model model, RedirectAttributes flash) {
		boolean esNuevo = socio.getId() == null;
		try {
			Socio guardado = socioService.guardar(socio);
			flash.addFlashAttribute("exito", (esNuevo ? "Se dio de alta a " : "Se guardaron los cambios de ")
					+ guardado.getNombre() + " " + guardado.getApellido());
			return "redirect:/socios";
		} catch (DatosInvalidosException | RecursoNoEncontradoException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("estados", EstadoCuota.values());
			if (!esNuevo) {
				model.addAttribute("inscripciones", inscripcionService.listarActivasDelSocio(socio.getId()));
			}
			return "socios/formulario";
		}
	}
}
